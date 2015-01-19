package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

public class LongManipulator implements DataWordManipulator<Long> {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getLongOperandAttr();
    }

    @Override
    public Long pop(StackFrame sf) {
        return sf.popLong();
    }

    @Override
    public void push(StackFrame sf, Long l) {
        sf.pushLong(l);
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setLongOperandAttr(attribute);
    }
}
