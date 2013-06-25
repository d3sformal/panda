package gov.nasa.jpf.abstraction.predicate.common;

class AccessPath extends Expression {
	public PathRootElement root;
	public PathElement tail;
	
	public AccessPath(String name) {
		root = new PathRootElement(name);
		tail = root;
	}
	
	public void append(PathElement element) {
		tail.next = element;
		element.previous = tail;
		tail = tail.next;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
}
