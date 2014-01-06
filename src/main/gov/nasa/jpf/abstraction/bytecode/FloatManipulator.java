package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.abstraction.Attribute;

public class FloatManipulator implements DataWordManipulator {
    @Override
    public Attribute getAttribute(StackFrame sf) {
        return (Attribute) sf.getOperandAttr();
    }

    @Override
    public Number pop(StackFrame sf) {
        return sf.popFloat();
    }

    @Override
    public void push(StackFrame sf, Number n) {
        sf.pushFloat(n.floatValue());
    }

    @Override
    public void setAttribute(StackFrame sf, Attribute attribute) {
        sf.setOperandAttr(attribute);
    }
}
