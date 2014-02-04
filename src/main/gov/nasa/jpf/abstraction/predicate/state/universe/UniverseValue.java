package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class UniverseValue {
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

        @Override
        public int hashCode() {
            return first.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Pair) {
                Object first = ((Pair) object).first;
                Object second = ((Pair) object).second;

                return this.first.getClass() == first.getClass() && this.second.getClass() == second.getClass() && this.first.equals(first) && this.second.equals(second);
            }

            return false;
        }
    }

    protected Set<Pair<Identifier, UniverseSlotKey>> parentSlots = new HashSet<Pair<Identifier, UniverseSlotKey>>();

    public void freeze() {
        frozen = true;
    }

    public Set<Pair<Identifier, UniverseSlotKey>> getParentSlots() {
        return parentSlots;
    }

    public void addParentSlot(Identifier parent, UniverseSlotKey slotKey) {
        parentSlots.add(new Pair<Identifier, UniverseSlotKey>(parent, slotKey));
    }

    public abstract UniverseIdentifier getIdentifier();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object object);
}
