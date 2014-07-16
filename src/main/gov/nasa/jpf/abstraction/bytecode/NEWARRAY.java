package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class NEWARRAY extends gov.nasa.jpf.jvm.bytecode.NEWARRAY {

    public NEWARRAY(int typeCode) {
        super(typeCode);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        Expression lengthExpression = ExpressionUtil.getExpression(sf.getOperandAttr());

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        if (RunDetector.isRunning()) {
            // Determine the unambiguous concrete array length from predicates
            PredicateAbstraction abs = PredicateAbstraction.getInstance();
            Integer lengthValue = abs.computePreciseExpressionValue(lengthExpression);

            if (lengthValue == null) {
                return ti.createAndThrowException("java.lang.IllegalArgumentException", "predicates do not specify exact array length");
            }

            // Check validity of the array length
            Predicate negative = LessThan.create(lengthExpression, Constant.create(0));
            TruthValue value = PredicateAbstraction.getInstance().processBranchingCondition(negative);

            if (value != TruthValue.FALSE) {
                return ti.createAndThrowException("java.lang.NegativeArraySizeException");
            } else {
                // Replace the original concrete value (possibly inconsistent with the abstraction) with the value derived from the abstraction
                sf.pop();
                sf.push(lengthValue);
            }
        }

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testNewArrayInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        ElementInfo array = ti.getElementInfo(sf.peek());
        AnonymousArray expression = AnonymousArray.create(new Reference(array), lengthExpression);

        PredicateAbstraction.getInstance().processNewObject(expression);

        sf = ti.getModifiableTopFrame();
        sf.setOperandAttr(expression);

        return actualNextInsn;
    }

}
