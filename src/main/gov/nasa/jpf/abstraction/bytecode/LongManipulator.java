package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

public class LongManipulator implements DataWordManipulator {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getLongOperandAttr();
    }

    @Override
    public Number pop(StackFrame sf) {
        return sf.popLong();
    }

    @Override
    public void push(StackFrame sf, Number n) {
        sf.pushLong(n.longValue());
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setLongOperandAttr(attribute);
    }
}
