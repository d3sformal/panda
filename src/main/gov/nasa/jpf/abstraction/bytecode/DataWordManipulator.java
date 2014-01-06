package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.abstraction.Attribute;

/**
 * Used to push an pop words of a fixed size and set their attributes
 */
public interface DataWordManipulator {
    Attribute getAttribute(StackFrame sf);
    Number pop(StackFrame sf);
    void push(StackFrame sf, Number n);
    void setAttribute(StackFrame sf, Attribute attribute);
}
