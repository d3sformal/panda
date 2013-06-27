package gov.nasa.jpf.abstraction.predicate.common;

import java.util.ArrayList;
import java.util.List;

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
	
	private void appendElement(PathMiddleElement element) {
		tail.next = element;
		element.previous = tail;
		tail = tail.next;
	}
	
	public void append(PathSubElement element) {
		appendElement(element);
	}
	
	public void append(PathIndexElement element) {
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
