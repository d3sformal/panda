package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;

public class PrimitiveLocalVariable extends PrimitiveValueSlot implements LocalVariable {
    private Root accessExpression;
    private int scope;

    public static LocalVariable.SlotKey slotKey = new LocalVariable.SlotKey();

    public PrimitiveLocalVariable(Root accessExpression, int scope) {
        this.accessExpression = accessExpression;
        this.scope = scope;

        setParent(this);
        setSlotKey(slotKey);
    }

    @Override
    public PrimitiveLocalVariable createShallowCopy() {
        PrimitiveLocalVariable copy = new PrimitiveLocalVariable(getAccessExpression(), getScope());

        for (PrimitiveValueIdentifier value : getPossiblePrimitiveValues()) {
            copy.addPossiblePrimitiveValue(value);
        }

        return copy;
    }

    @Override
    public Root getAccessExpression() {
        return accessExpression;
    }

    @Override
    public int getScope() {
        return scope;
    }

    @Override
    public int compareTo(Identifier id) {
        if (id instanceof PrimitiveLocalVariable) {
            PrimitiveLocalVariable var = (PrimitiveLocalVariable) id;

            return getAccessExpression().getName().compareTo(var.getAccessExpression().getName());
        }

        return Identifier.Ordering.compare(this, id);
    }

    @Override
    public String toString() {
        return accessExpression.toString();
    }
}
