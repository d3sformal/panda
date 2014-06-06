package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import java.util.LinkedList;
import java.util.List;

public class MULTIANEWARRAY extends gov.nasa.jpf.jvm.bytecode.MULTIANEWARRAY {

    public MULTIANEWARRAY (String typeName, int dimensions) {
        super(typeName, dimensions);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        return ti.createAndThrowException("java.lang.UnsupportedOperationException", "Multi-dimensional arrays not supported (use Object[])");
    }

    /*
    @Override
    public Instruction execute(ThreadInfo ti) {
        StackFrame sf = ti.getTopFrame();
        List<Expression> attrs = new LinkedList<Expression>();

        for (int i = getDimensions() - 1; i >= 0 ; --i) {
            Expression attr = (Expression) sf.getOperandAttr(i);

            if (attr == null) {
                attr = new EmptyExpression();
            }

            attrs.add(attr);
        }

        Expression attr = attrs.get(attrs.size() - 1);
        attrs.remove(attrs.size() - 1);

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        Instruction actualNextInsn = super.execute(ti);

        if (JPFInstructionAdaptor.testNewArrayInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        ElementInfo array = ti.getElementInfo(sf.peek());
        AnonymousArray expression = AnonymousArray.create(new Reference(array), attr.getExpression());

        PredicateAbstraction.getInstance().processNewObject(expression);

        sf = ti.getModifiableTopFrame();
        sf.setOperandAttr(new Expression(null, expression));

        // ALL ELEMENTS ARE NULL
        setArrayExpressions(ti, array, attrs);

        return actualNextInsn;
    }

    private void setArrayExpressions(ThreadInfo ti, ElementInfo array, List<Expression> subList) {
        if (subList.isEmpty()) return;

        ArrayFields fields = array.getArrayFields();
        int size = array.arrayLength();

        Expression attr = subList.get(subList.size() - 1);

        for (int i = 0; i < size; ++i) {
            fields.addFieldAttr(size, i, attr);

            if (array.isReferenceArray()) {
                ElementInfo subArray = ti.getElementInfo(array.getReferenceElement(i));

                setArrayExpressions(ti, subArray, subList.subList(1, subList.size()));
            }
        }

        //subList.remove(subList.size() - 1);
    }
    */
}
