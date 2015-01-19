package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

public class DoubleManipulator implements DataWordManipulator<Double> {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getLongOperandAttr();
    }

    @Override
    public Double pop(StackFrame sf) {
        return sf.popDouble();
    }

    @Override
    public void push(StackFrame sf, Double d) {
        sf.pushDouble(d);
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setLongOperandAttr(attribute);
    }
}
