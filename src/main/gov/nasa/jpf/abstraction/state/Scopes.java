package gov.nasa.jpf.abstraction.state;

/**
 * An interface for stack of scopes allowing to add new or return to previously stored scopes.
 */
public interface Scopes extends Cloneable {
    public Scope top();
    public Scope top(int i);
    public void pop();
    public void push(String name, Scope scope);
    public void replace(int i, Scope scope);
    public int count();
    public void print();
    public Scopes clone();
}
