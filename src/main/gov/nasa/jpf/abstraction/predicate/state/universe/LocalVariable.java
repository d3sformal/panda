package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;

public interface LocalVariable extends Identifier, UniverseSlot {
    public class SlotKey implements UniverseSlotKey {
    }

    @Override
    public LocalVariable createShallowCopy();

    public Root getAccessExpression();
    public int getScope();
}
