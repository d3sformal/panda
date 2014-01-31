package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.UUID;

public class PrimitiveValueIdentifier implements UniverseIdentifier {
    private UUID uuid;

    public UUID getUUID() {
        return uuid;
    }
}
