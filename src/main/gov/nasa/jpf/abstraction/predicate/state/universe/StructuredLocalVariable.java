package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.Root;

public class StructuredLocalVariable extends StructuredValueSlot implements LocalVariable {
    private Root accessExpression;

    public StructuredLocalVariable(Root accessExpression) {
        this.accessExpression = accessExpression;

        setParent(this);
        setSlotKey(null);
    }

    @Override
    public Root getAccessExpression() {
        return accessExpression;
    }
}
