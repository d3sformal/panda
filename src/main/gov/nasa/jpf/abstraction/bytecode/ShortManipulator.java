package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.StackFrame;

public class ShortManipulator implements DataWordManipulator {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getOperandAttr();
    }

    @Override
    public Number pop(StackFrame sf) {
        return sf.pop();
    }

    @Override
    public void push(StackFrame sf, Number n) {
        sf.push(n.shortValue());
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setOperandAttr(attribute);
    }
}
