package gov.nasa.jpf.abstraction.predicate.common;

import java.util.ArrayList;
import java.util.List;

public class AccessPath extends Expression {
	public static enum NotationPolicy {
		DOT_NOTATION,
		FUNCTION_NOTATION
	}
	
	public static NotationPolicy policy = NotationPolicy.FUNCTION_NOTATION;
	
	protected AccessPathRootElement root;
	protected AccessPathElement tail;
	
	protected AccessPathRootElement createRootElement(String name) {
		return new DefaultAccessPathRootElement(name);
	}
	
	protected AccessPath() {
	}
	
	public AccessPath(String name) {
		root = createRootElement(name);
		tail = root;
	}
	
	private void appendElement(AccessPathMiddleElement element) {
		tail.setNext(element);
		element.setPrevious(tail);
		tail = element;
	}
	
	protected AccessPathMiddleElement createSubElement(String name) {
		return new DefaultAccessPathSubElement(name);
	}
	
	protected AccessPathMiddleElement createIndexElement(Expression index) {
		return new DefaultConcretePathIndexElement(index);
	}
	
	public void appendSubElement(String name) {
		appendElement(createSubElement(name));
	}
	
	public void appendIndexElement(Expression index) {
		appendElement(createIndexElement(index));

		paths.addAll(index.paths);
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
	
	@Override
	public int hashCode() {
		return toString(NotationPolicy.DOT_NOTATION).hashCode();
	}
	
	@Override
	public boolean equals(Object path) {
		return toString().equals(path.toString());
	}

}