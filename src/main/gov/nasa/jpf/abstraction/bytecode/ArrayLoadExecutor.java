/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.bytecode;

import java.util.HashSet;
import java.util.Set;

import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.choice.BreakGenerator;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;

import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.BranchingExecutionHelper;
import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.Indexed;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.RunDetector;

public class ArrayLoadExecutor {
    private ElementInfo ei;
    private AccessExpression array;
    private int originalIndex;
    private int concreteIndex;
    private int abstractIndex;
    private Expression index;
    private AccessExpression path;
    private Predicate inBounds;
    private TruthValue indexInBounds;

    enum Phase {
        PHASE1,
        PHASE2,
        PHASE3,
        PHASE4,
        PHASE5
    }

    class State {
        Phase phase;
        ElementInfo ei;
        AccessExpression array;
        int originalIndex;
        int concreteIndex;
        int abstractIndex;
        Expression index;
        AccessExpression path;
        Predicate inBounds;
        TruthValue indexInBounds;
    }

    private IndexSelector indexSelector;
    private static final String ARRAY_INDEX_OUT_OF_BOUNDS = "java.lang.ArrayIndexOutOfBoundsException";
    private static final String BOUNDS_CHECK = "Checking array bounds";
    private static final String NULL_POINTER_EXCEPTION = "java.lang.NullPointerException";

    public ArrayLoadExecutor(IndexSelector indexSelector) {
        this.indexSelector = indexSelector;
    }

    protected void storeState(SystemState ss, Phase phase) {
        State s = new State();

        s.phase = phase;
        s.ei = ei;
        s.array = array;
        s.originalIndex = originalIndex;
        s.concreteIndex = concreteIndex;
        s.abstractIndex = abstractIndex;
        s.index = index;
        s.path = path;
        s.inBounds = inBounds;
        s.indexInBounds = indexInBounds;

        ss.getNextChoiceGenerator().setAttr(s);
    }

    protected Phase restoreState(SystemState ss) {
        State s = (State) ss.getChoiceGenerator().getAttr();

        ei = s.ei;
        array = s.array;
        originalIndex = s.originalIndex;
        concreteIndex = s.concreteIndex;
        abstractIndex = s.abstractIndex;
        index = s.index;
        path = s.path;
        inBounds = s.inBounds;
        indexInBounds = s.indexInBounds;

        return s.phase;
    }

    public Instruction execute(ArrayLoadInstruction load, ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        if (ti.isFirstStepInsn()) {
            switch (restoreState(ss)) {
                case PHASE1: return phase1(load, ti);
                case PHASE2: return phase2(load, ti);
                case PHASE3: return phase3(load, ti);
                case PHASE4: return phase4(load, ti);
                case PHASE5: return phase5(load, ti);
            }
        }

        ei = ti.getElementInfo(sf.peek(1));

        if (ei == null) {
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(Equals.create(array, NullExpression.create()), load.getSelf().getMethodInfo(), load.getSelf().getNext().getPosition());

            return ti.createAndThrowException(NULL_POINTER_EXCEPTION, "Null dereference");
        }

        array = ExpressionUtil.getAccessExpression(sf.getOperandAttr(1));
        index = ExpressionUtil.getExpression(sf.getOperandAttr(0));
        path = DefaultArrayElementRead.create(array, index);

        return phase1(load, ti);
    }

    protected Instruction phase1(ArrayLoadInstruction load, ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        concreteIndex = sf.peek();
        abstractIndex = -1;

        if (RunDetector.isRunning()) {
            PredicateAbstraction abs = PredicateAbstraction.getInstance();
            MethodFrameSymbolTable sym = abs.getSymbolTable().get(0);

            if (indexSelector.makeChoices(ti, ss, abs, sym, array, index)) {
                storeState(ss, Phase.PHASE1);

                return load.getSelf();
            }

            abstractIndex = indexSelector.getIndex();
            originalIndex = sf.pop();

            sf.push(abstractIndex);
        }

        return phase2(load, ti);
    }


    protected Instruction phase2(ArrayLoadInstruction load, ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();

        if (RunDetector.isRunning() && !RunDetector.isInLibrary(ThreadInfo.getCurrentThread())) {
            // i >= 0 && i < a.length
            inBounds = Conjunction.create(
                Negation.create(LessThan.create(index, Constant.create(0))),
                LessThan.create(index, getUpperBound(ei, array))
            );

            if (ti.isFirstStepInsn() && ss.getChoiceGenerator().getId().equals(BOUNDS_CHECK)) {
                indexInBounds = (Integer) ss.getChoiceGenerator().getNextChoice() == 0 ? TruthValue.FALSE : TruthValue.TRUE;
            } else {
                indexInBounds = PredicateAbstraction.getInstance().processBranchingCondition(load.getSelf().getPosition(), inBounds);

                if (indexInBounds == TruthValue.UNKNOWN) {
                    ChoiceGenerator<?> cg = new AbstractChoiceGenerator();
                    cg.setId(BOUNDS_CHECK);

                    if (ti.isFirstStepInsn()) {
                        ss.setForced(true);
                    }

                    ss.setNextChoiceGenerator(cg);

                    storeState(ss, Phase.PHASE2);

                    return load.getSelf();
                }
            }
        }

        return phase3(load, ti);
    }

    protected Instruction phase3(ArrayLoadInstruction load, ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();

        if (RunDetector.isRunning() && !RunDetector.isInLibrary(ThreadInfo.getCurrentThread())) {
            boolean concretePass = (originalIndex >= 0 && originalIndex < ei.arrayLength());
            boolean abstractPass = (indexInBounds == TruthValue.TRUE);

            if (!abstractPass) {
                PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(inBounds, TruthValue.FALSE), load.getSelf().getMethodInfo(), load.getSelf().getPosition());
                Instruction insn = BranchingExecutionHelper.synchronizeConcreteAndAbstractExecutions(ti, inBounds, concretePass, abstractPass, ti.getPC().getNext(), load.getSelf());

                if (insn == load.getSelf()) {
                    storeState(ss, Phase.PHASE3);

                    return insn;
                }

                return ThreadInfo.getCurrentThread().createAndThrowException(ARRAY_INDEX_OUT_OF_BOUNDS, "Cannot ensure: " + inBounds);
            }
        }

        return phase4(load, ti);
    }

    protected Instruction phase4(ArrayLoadInstruction load, ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();

        if (RunDetector.isRunning()) {
            Predicate condition = Equals.create(index, Constant.create(abstractIndex));

            if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
                System.out.println(path.toString(gov.nasa.jpf.abstraction.common.Notation.DOT_NOTATION) + " (concrete: " + index + " = " + concreteIndex + ", abstract: " + index + " = " + abstractIndex + ") " + (concreteIndex == abstractIndex ? "Pass" : "Cut"));
            }

            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(condition, load.getSelf().getMethodInfo(), load.getSelf().getNext().getPosition());

            Instruction insn = BranchingExecutionHelper.synchronizeConcreteAndAbstractExecutions(ti, condition, concreteIndex == abstractIndex, true, load.getSelf().getNext(), load.getSelf());

            if (insn == load.getSelf()) {
                storeState(ss, Phase.PHASE5); // Skip to the next phase (this is just break)

                return insn;
            }
        }

        return phase5(load, ti);
    }

    protected Instruction phase5(ArrayLoadInstruction load, ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(load.getSelf(), ti);
        Instruction actualNextInsn = load.executeConcrete(ti);

        if (JPFInstructionAdaptor.testArrayElementInstructionAbort(load.getSelf(), ti, expectedNextInsn, actualNextInsn)) {
            storeState(ss, Phase.PHASE5);

            return actualNextInsn;
        }

        load.setAttribute(sf, path);

        return load.getSelf().getNext();
    }

    protected Expression getUpperBound(ElementInfo ei, AccessExpression array) {
        // Builtin static (constant-length) arrays
        // such as SWITCHMAP for enum types
        if (array instanceof ObjectFieldRead && ((ObjectFieldRead) array).getField().getName().startsWith("$SwitchMap")) {
            return Constant.create(ei.arrayLength());
        } else {
            return DefaultArrayLengthRead.create(array);
        }
    }

    public void push(ArrayLoadInstruction load, StackFrame sf, ElementInfo ei, int someIndex) throws ArrayIndexOutOfBoundsExecutiveException {
        load.pushConcrete(sf, ei, someIndex);
    }
}
