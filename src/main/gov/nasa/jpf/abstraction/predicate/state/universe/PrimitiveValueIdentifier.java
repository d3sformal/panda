package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.UUID;

public class PrimitiveValueIdentifier implements UniverseIdentifier {
    private UUID uuid = UUID.randomUUID();

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public int compareTo(Identifier id) {
        if (id instanceof PrimitiveValueIdentifier) {
            PrimitiveValueIdentifier p = (PrimitiveValueIdentifier) id;

            return getUUID().compareTo(p.getUUID());
        }

        return Identifier.Ordering.compare(this, id);
    }
}
