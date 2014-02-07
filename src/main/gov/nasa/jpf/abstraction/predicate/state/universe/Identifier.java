package gov.nasa.jpf.abstraction.predicate.state.universe;

public interface Identifier extends Comparable<Identifier> {
    public static class Ordering {
        public static int compare(Identifier i1, Identifier i2) {
            return i1.getClass().getName().compareTo(i2.getClass().getName());
        }
    }

    public abstract int hashCode();
    public abstract boolean equals(Object object);
}
