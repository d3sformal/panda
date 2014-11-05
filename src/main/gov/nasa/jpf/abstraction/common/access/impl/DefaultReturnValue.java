package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.vm.Instruction;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;

/**
 * A grammar element representing a keyword 'return' used in predicates over return values of methods
 */
public class DefaultReturnValue extends DefaultRoot implements ReturnValue {

    private boolean isReturnFromCurrentScope = true;

    protected DefaultReturnValue() {
        super("return");
    }

    protected DefaultReturnValue(String name) {
        super(name);

        this.isReturnFromCurrentScope = false;
    }

    public static DefaultReturnValue create() {
        return new DefaultReturnValue();
    }

    public static DefaultReturnValue create(String name) {
        return new DefaultReturnValue(name);
    }

    public static DefaultReturnValue create(Instruction pc) {
        return create("return_pc" + pc.getInstructionIndex());
    }

    @Override
    public boolean isReturnFromCurrentScope() {
        return isReturnFromCurrentScope;
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        if (o instanceof ReturnValue) {
            ReturnValue r = (ReturnValue) o;

            return getName().equals(r.getName());
        }

        return false;
    }

    @Override
    public DefaultReturnValue createShallowCopy() {
        return this;
    }
}
