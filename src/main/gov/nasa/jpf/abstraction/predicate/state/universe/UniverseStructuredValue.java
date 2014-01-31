package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class UniverseStructuredValue {
    protected boolean frozen = false;

    public class Pair<S, T> {
        private S first;
        private T second;

        public Pair(S first, T second) {
            this.first = first;
            this.second = second;
        }

        public S getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }
    }

    private Set<Pair<Identifier, UniverseSlotKey>> parentSlots = new HashSet<Pair<Identifier, UniverseSlotKey>>();

    public void freeze() {
        frozen = true;
    }

    public Set<Pair<Identifier, UniverseSlotKey>> getParentSlots() {
        return parentSlots;
    }

    public abstract StructuredValueIdentifier getIdentifier();

    public abstract UniverseSlot getSlot(UniverseSlotKey key);

    public abstract Map<? extends UniverseSlotKey, ? extends UniverseSlot> getSlots();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object object);
}
