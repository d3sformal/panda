package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Used to push an pop words of a fixed size and set their attributes
 */
public interface DataWordManipulator<T> {
    Expression getExpression(StackFrame sf);
    T pop(StackFrame sf);
    void push(StackFrame sf, T t);
    void setExpression(StackFrame sf, Expression attribute);
}
