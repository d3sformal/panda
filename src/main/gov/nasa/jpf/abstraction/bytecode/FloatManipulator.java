package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

public class FloatManipulator implements DataWordManipulator<Float> {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getOperandAttr();
    }

    @Override
    public Float pop(StackFrame sf) {
        return sf.popFloat();
    }

    @Override
    public void push(StackFrame sf, Float f) {
        sf.pushFloat(f);
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setOperandAttr(attribute);
    }
}
