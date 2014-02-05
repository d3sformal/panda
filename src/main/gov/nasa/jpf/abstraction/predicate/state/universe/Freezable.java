package gov.nasa.jpf.abstraction.predicate.state.universe;

public interface Freezable {
    public void freeze();
    public boolean isFrozen();
    public Freezable createShallowCopy();
}
