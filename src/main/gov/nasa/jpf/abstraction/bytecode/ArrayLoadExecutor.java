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
import gov.nasa.jpf.vm.choice.IntChoiceFromList;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;

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
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.Indexed;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.RunDetector;

public class ArrayLoadExecutor {
    private AccessExpression array;
    private Expression index;
    private AccessExpression path;

    private IndexSelector indexSelector;
    private static final String ARRAY_INDEX_OUT_OF_BOUNDS = "java.lang.ArrayIndexOutOfBoundsException";

    public ArrayLoadExecutor(IndexSelector indexSelector) {
        this.indexSelector = indexSelector;
    }

    public Instruction execute(ArrayLoadInstruction load, ThreadInfo ti) {
        StackFrame sf = ti.getModifiableTopFrame();

        array = ExpressionUtil.getAccessExpression(sf.getOperandAttr(1));
        index = ExpressionUtil.getExpression(sf.getOperandAttr(0));
        path = DefaultArrayElementRead.create(array, index);

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(load.getSelf(), ti);

        SystemState ss = ti.getVM().getSystemState();

        int concreteIndex = sf.peek();
        int abstractIndex = -1;

        if (RunDetector.isRunning()) {
            PredicateAbstraction abs = PredicateAbstraction.getInstance();
            MethodFrameSymbolTable sym = abs.getSymbolTable().get(0);

            if (indexSelector.makeChoices(ti, ss, abs, sym, array, index)) {
                return load.getSelf();
            }

            abstractIndex = indexSelector.getIndex();

            sf.pop();
            sf.push(abstractIndex);
        }

        Instruction actualNextInsn = load.executeConcrete(ti);

        if (JPFInstructionAdaptor.testArrayElementInstructionAbort(load.getSelf(), ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        sf.setOperandAttr(path);

        if (RunDetector.isRunning() && PandaConfig.getInstance().pruneInfeasibleBranches()) {
            Predicate condition = Equals.create(index, Constant.create(abstractIndex));

            if (PandaConfig.getInstance().enabledVerbose()) {
                System.out.println(path.toString(gov.nasa.jpf.abstraction.common.Notation.DOT_NOTATION) + " (concrete: " + index + " = " + concreteIndex + ", abstract: " + index + " = " + abstractIndex + ") " + (concreteIndex == abstractIndex ? "Pass" : "Cut"));
            }

            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(condition, load.getSelf().getMethodInfo(), actualNextInsn.getPosition());
            BranchingExecutionHelper.synchronizeConcreteAndAbstractExecutions(ti, condition, concreteIndex == abstractIndex, true, actualNextInsn, load.getSelf());
        }

        return actualNextInsn;
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
        if (RunDetector.isRunning() && !RunDetector.isInLibrary(ThreadInfo.getCurrentThread())) {
            // i >= 0 && i < a.length
            Predicate inBounds = Conjunction.create(
                Negation.create(LessThan.create(index, Constant.create(0))),
                LessThan.create(index, getUpperBound(ei, array))
            );

            TruthValue value = PredicateAbstraction.getInstance().processBranchingCondition(load.getSelf().getPosition(), inBounds);

            if (value != TruthValue.TRUE) {
                PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(inBounds, TruthValue.FALSE), load.getSelf().getMethodInfo(), load.getSelf().getPosition());
                throw new ArrayIndexOutOfBoundsExecutiveException(ThreadInfo.getCurrentThread().createAndThrowException(ARRAY_INDEX_OUT_OF_BOUNDS, "Cannot ensure: " + inBounds));
            }
        }

        load.pushConcrete(sf, ei, someIndex);
    }
}
