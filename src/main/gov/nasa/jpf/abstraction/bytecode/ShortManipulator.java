package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

public class ShortManipulator implements DataWordManipulator<Short> {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getOperandAttr();
    }

    @Override
    public Short pop(StackFrame sf) {
        return (short) sf.pop();
    }

    @Override
    public void push(StackFrame sf, Short s) {
        sf.push(s);
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setOperandAttr(attribute);
    }
}
