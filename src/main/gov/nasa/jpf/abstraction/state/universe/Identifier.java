package gov.nasa.jpf.abstraction.state.universe;

public interface Identifier extends Comparable<Identifier> {

    // sorting order: heap objects < class names
    public static class Ordering {
        // Identifier.getClass().getName(): Reference or ClassName (for example)
        public static int compare(Identifier i1, Identifier i2) {
            return (-1) * i1.getClass().getName().compareTo(i2.getClass().getName());
        }
    }

    public abstract int hashCode();
    public abstract boolean equals(Object object);
}
