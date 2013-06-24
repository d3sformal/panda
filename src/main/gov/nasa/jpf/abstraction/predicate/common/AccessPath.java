package gov.nasa.jpf.abstraction.predicate.common;

class AccessPath extends Expression {
	public PathRootElement root;
	public PathElement tail;
	
	public AccessPath(String name) {
		root = new PathRootElement(name);
		tail = root;
	}
	
	public void appendField(String name) {
		tail.next = new PathFieldElement(tail, name);
		tail = tail.next;
	}
	
	public void appendIndex(Expression expression) {
		tail.next = new PathIndexElement(tail, expression);
		tail = tail.next;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
}
