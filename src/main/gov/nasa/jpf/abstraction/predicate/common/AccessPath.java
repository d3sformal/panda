package gov.nasa.jpf.abstraction.predicate.common;

import java.util.ArrayList;
import java.util.List;

public class AccessPath extends Expression {
	public static enum NotationPolicy {
		DOT_NOTATION,
		FUNCTION_NOTATION
	}
	
	public static NotationPolicy policy = NotationPolicy.FUNCTION_NOTATION;
	
	public AccessPathRootElement root;
	public AccessPathElement tail;
	
	public AccessPath(String name) {
		root = new AccessPathRootElement(name);
		tail = root;
	}
	
	private void appendElement(AccessPathMiddleElement element) {
		tail.next = element;
		element.previous = tail;
		tail = tail.next;
	}
	
	public void append(AccessPathSubElement element) {
		appendElement(element);
	}
	
	public void append(AccessPathIndexElement element) {
		appendElement(element);
		
		paths.addAll(element.index.paths);
	}
	
	@Override
	public List<AccessPath> getPaths() {
		List<AccessPath> ret = new ArrayList<AccessPath>();
		
		ret.addAll(paths);
		ret.add(this);
		
		return ret;
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
