package gov.nasa.jpf.abstraction.predicate.state;

public interface Scoped {
	public void processMethodCall();
	public void processMethodReturn();
	public void store(Scope scope);
	public void restore(Scope scope);
	public Scope memorize();
}
