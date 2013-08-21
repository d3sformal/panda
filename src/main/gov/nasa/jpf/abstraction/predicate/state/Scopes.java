package gov.nasa.jpf.abstraction.predicate.state;

public interface Scopes extends Cloneable {
	public Scope top();
	public Scope top(int i);
	public void pop();
	public void push(Scope scope);
	public int count();
	public Scopes clone();
}
