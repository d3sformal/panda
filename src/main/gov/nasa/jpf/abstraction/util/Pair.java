package gov.nasa.jpf.abstraction.util;

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
