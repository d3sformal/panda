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
    public Root getAccessExpression() {
        return accessExpression;
    }

    @Override
    public FlatSymbolTable getScope() {
        return scope;
    }
}
