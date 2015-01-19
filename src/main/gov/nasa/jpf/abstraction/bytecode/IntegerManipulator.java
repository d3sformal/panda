package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

public class IntegerManipulator implements DataWordManipulator<Integer> {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getOperandAttr();
    }

    @Override
    public Integer pop(StackFrame sf) {
        return sf.pop();
    }

    @Override
    public void push(StackFrame sf, Integer i) {
        sf.push(i);
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setOperandAttr(attribute);
    }
}
