package gov.nasa.jpf.abstraction.predicate.state;

/**
 * An interface for stack of scopes allowing to add new or return to previously stored scopes.
 */
public interface Scopes extends Cloneable {
	public Scope top();
	public Scope top(int i);
	public void pop();
	public void push(Scope scope);
	public int count();
	public Scopes clone();
}
