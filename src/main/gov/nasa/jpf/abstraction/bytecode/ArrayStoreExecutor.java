package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.BranchingExecutionHelper;
import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;

public class ArrayStoreExecutor {
    private static final String ARRAY_INDEX_OUT_OF_BOUNDS = "java.lang.ArrayIndexOutOfBoundsException";
    private static final String NULL_POINTER_EXCEPTION = "java.lang.NullPointerException";

    public Instruction execute(ArrayStoreInstruction store, ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getTopFrame();
        Expression from = store.getSourceExpression(sf);
        Expression index = store.getIndexExpression(sf);
        int concreteIndex = store.getIndex(sf);
        AccessExpression to = store.getArrayExpression(sf);

        ElementInfo ei = store.getArray(sf);

        if (ei == null) {
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(Equals.create(from, NullExpression.create()), store.getSelf().getMethodInfo(), store.getSelf().getNext().getPosition());

            return ti.createAndThrowException(NULL_POINTER_EXCEPTION, "Null dereference");
        }

        ArrayFields fields = ei.getArrayFields();

        int originalIndex = store.getIndex(sf);

        for (int i = 0; i < fields.arrayLength(); ++i) {
            fields.addFieldAttr(fields.arrayLength(), i, from);
        }

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(store.getSelf(), ti);

        if (RunDetector.isRunning() && !RunDetector.isInLibrary(ti)) {
            Predicate inBounds = Conjunction.create(
                Negation.create(LessThan.create(index, Constant.create(0))),
                LessThan.create(index, DefaultArrayLengthRead.create(to))
            );

            TruthValue indexInBounds;

            if (!ti.isFirstStepInsn()) {
                indexInBounds = PredicateAbstraction.getInstance().processBranchingCondition(store.getSelf().getPosition(), inBounds);

                if (indexInBounds == TruthValue.UNKNOWN) {
                    ChoiceGenerator<?> cg = new AbstractChoiceGenerator();

                    ss.setNextChoiceGenerator(cg);

                    return store.getSelf();
                }
            } else {
                ChoiceGenerator<?> cg = ss.getChoiceGenerator();

                indexInBounds = (Integer) cg.getNextChoice() == 0 ? TruthValue.FALSE : TruthValue.TRUE;
            }

            boolean concretePass = (originalIndex >= 0 && originalIndex < ei.arrayLength());
            boolean abstractPass = (indexInBounds == TruthValue.TRUE);

            if (!abstractPass) {
                PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(inBounds, TruthValue.FALSE), store.getSelf().getMethodInfo(), store.getSelf().getPosition());
                Instruction insn = BranchingExecutionHelper.synchronizeConcreteAndAbstractExecutions(ti, inBounds, concretePass, abstractPass, ti.getPC().getNext(), store.getSelf());

                if (insn == store.getSelf()) {
                    return insn;
                }

                return ThreadInfo.getCurrentThread().createAndThrowException(ARRAY_INDEX_OUT_OF_BOUNDS, "Cannot ensure: " + inBounds);
            }
        }

        // Here we may write into a different index than those corresponding to abstract state
        // Only if we do not apply pruning of infeasible paths (inconsistent concrete/abstract state)
        Instruction actualNextInsn = store.executeConcrete(ti);

        if (JPFInstructionAdaptor.testArrayElementInstructionAbort(store.getSelf(), ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        AccessExpression element = DefaultArrayElementRead.create(to, index);
        AccessExpression exactElement = DefaultArrayElementRead.create(to, Constant.create(concreteIndex));

        if (store instanceof AASTORE) {
            // Element indices are derived from predicates in this method call
            PredicateAbstraction.getInstance().processObjectStore(store.getSelf().getMethodInfo(), store.getSelf().getPosition(), actualNextInsn.getMethodInfo(), actualNextInsn.getPosition(), from, element, exactElement);
        } else {
            // Element indices are derived from predicates in this method call
            PredicateAbstraction.getInstance().processPrimitiveStore(store.getSelf().getMethodInfo(), store.getSelf().getPosition(), actualNextInsn.getMethodInfo(), actualNextInsn.getPosition(), from, element);
        }

        AnonymousExpressionTracker.notifyPopped(from);
        AnonymousExpressionTracker.notifyPopped(to);

        return actualNextInsn;
    }
}
