package gov.nasa.jpf.abstraction.predicate.common;

public class AccessPath extends Expression {
	public static enum NotationPolicy {
		DOT_NOTATION,
		FUNCTION_NOTATION
	}
	
	public static NotationPolicy policy = NotationPolicy.FUNCTION_NOTATION;
	
	public PathRootElement root;
	public PathElement tail;
	
	public AccessPath(String name) {
		root = new PathRootElement(name);
		tail = root;
	}
	
	public void append(PathMiddleElement element) {
		tail.next = element;
		element.previous = tail;
		tail = tail.next;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
	
	public String toString(NotationPolicy policy) {
		NotationPolicy original = AccessPath.policy;
		
		AccessPath.policy = policy;
		
		String ret = toString();
		
		AccessPath.policy = original;
		
		return ret;
	}
}
