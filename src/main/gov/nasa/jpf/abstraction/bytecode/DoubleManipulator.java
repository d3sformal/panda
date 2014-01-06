package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.abstraction.Attribute;

public class DoubleManipulator implements DataWordManipulator {
    @Override
    public Attribute getAttribute(StackFrame sf) {
        return (Attribute) sf.getLongOperandAttr();
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
    public void setAttribute(StackFrame sf, Attribute attribute) {
        sf.setLongOperandAttr(attribute);
    }
}
