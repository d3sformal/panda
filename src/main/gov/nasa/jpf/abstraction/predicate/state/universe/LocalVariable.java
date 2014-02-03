package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.Root;

public interface LocalVariable extends Identifier, UniverseSlot {
    public Root getAccessExpression();
}
