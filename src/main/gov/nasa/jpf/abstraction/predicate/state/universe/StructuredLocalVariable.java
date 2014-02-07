package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;

public class StructuredLocalVariable extends StructuredValueSlot implements LocalVariable {
    private Root accessExpression;
    private FlatSymbolTable scope;

    public static LocalVariable.SlotKey slotKey = new LocalVariable.SlotKey();

    public StructuredLocalVariable(Root accessExpression, FlatSymbolTable scope) {
        this.accessExpression = accessExpression;
        this.scope = scope;

        setParent(this);
        setSlotKey(slotKey);
    }

    @Override
    public StructuredLocalVariable createShallowCopy() {
        StructuredLocalVariable copy = new StructuredLocalVariable(getAccessExpression(), getScope());

        for (StructuredValueIdentifier value : getPossibleStructuredValues()) {
            copy.addPossibleStructuredValue(value);
        }

        return copy;
    }

    @Override
    public Root getAccessExpression() {
        return accessExpression;
    }

    @Override
    public FlatSymbolTable getScope() {
        return scope;
    }

    @Override
    public int compareTo(Identifier id) {
        if (id instanceof StructuredLocalVariable) {
            StructuredLocalVariable var = (StructuredLocalVariable) id;

            return getAccessExpression().getName().compareTo(var.getAccessExpression().getName());
        }

        return Identifier.Ordering.compare(this, id);
    }
}
