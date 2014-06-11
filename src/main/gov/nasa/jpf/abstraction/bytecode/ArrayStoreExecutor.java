package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ArrayStoreExecutor {
    private static final String ARRAY_INDEX_OUT_OF_BOUNDS = "java.lang.ArrayIndexOutOfBoundsException";

    public Instruction execute(ArrayStoreInstruction store, ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression from = store.getSourceExpression(sf);
        Expression index = store.getIndexExpression(sf);
        AccessExpression to = store.getArrayExpression(sf);

        ElementInfo ei = store.getArray(sf);
        ArrayFields fields = ei.getArrayFields();

        for (int i = 0; i < fields.arrayLength(); ++i) {
            fields.addFieldAttr(fields.arrayLength(), i, from);
        }

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(store.getSelf(), ti);

        if (RunDetector.isRunning() && !RunDetector.isInLibrary(ti)) {
            Predicate inBounds = Conjunction.create(
                Negation.create(LessThan.create(index, Constant.create(0))),
                LessThan.create(index, DefaultArrayLengthRead.create(to))
            );

            TruthValue value = PredicateAbstraction.getInstance().processBranchingCondition(inBounds);

            if (value != TruthValue.TRUE) {
                throw new ArrayIndexOutOfBoundsExecutiveException(ThreadInfo.getCurrentThread().createAndThrowException(ARRAY_INDEX_OUT_OF_BOUNDS, "Cannot ensure: " + inBounds));
            }
        }

        // Here we may write into a different index than those corresponding to abstract state
        // Only if we do not apply pruning of infeasible paths (inconsistent concrete/abstract state)
        Instruction actualNextInsn = store.executeConcrete(ti);

        if (JPFInstructionAdaptor.testArrayElementInstructionAbort(store.getSelf(), ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        AccessExpression element = DefaultArrayElementRead.create(to, index);

        if (store instanceof AASTORE) {
            // Element indices are derived from predicates in this method call
            PredicateAbstraction.getInstance().processObjectStore(from, element);
        } else {
            // Element indices are derived from predicates in this method call
            PredicateAbstraction.getInstance().processPrimitiveStore(from, element);
        }

        AnonymousExpressionTracker.notifyPopped(from);
        AnonymousExpressionTracker.notifyPopped(to);

        return actualNextInsn;
    }
}
