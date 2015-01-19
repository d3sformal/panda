package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.common.Expression;

public class CharacterManipulator implements DataWordManipulator<Character> {
    @Override
    public Expression getExpression(StackFrame sf) {
        return (Expression) sf.getOperandAttr();
    }

    @Override
    public Character pop(StackFrame sf) {
        return (char) sf.pop();
    }

    @Override
    public void push(StackFrame sf, Character c) {
        sf.push((int) c);
    }

    @Override
    public void setExpression(StackFrame sf, Expression attribute) {
        sf.setOperandAttr(attribute);
    }
}
