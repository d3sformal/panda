package gov.nasa.jpf.abstraction.predicate.state;

public interface Scope extends Cloneable {
	public Scope clone();
	public int count();
}
