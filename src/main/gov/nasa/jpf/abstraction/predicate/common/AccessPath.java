package gov.nasa.jpf.abstraction.predicate.common;

import java.util.ArrayList;
import java.util.List;

public class AccessPath extends Expression implements Cloneable {
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
	
	protected void appendElement(AccessPathMiddleElement element) {
		tail.setNext(element);
		element.setPrevious(tail);
		tail = element;
	}
	
	public void appendSubElement(String name) {
		appendElement(new DefaultAccessPathSubElement(name));
	}
	
	public void appendIndexElement(Expression index) {
		appendElement(new DefaultAccessPathIndexElement(index));

		paths.addAll(index.paths);
	}
	
	public static void reRoot(AccessPath path, AccessPath prefix, String name) {
		AccessPathRootElement newRoot = path.createRootElement(name);
		
		AccessPathElement rootPrefix = prefix.root;
		AccessPathElement rootThis = path.root;
		
		while (rootPrefix.equals(rootThis)) {
			rootPrefix = rootPrefix.getNext();
			rootThis = rootThis.getNext();
		}
		
		AccessPathMiddleElement next = (AccessPathMiddleElement) rootThis;
		
		path.root = newRoot;
		path.tail = newRoot;
		
		newRoot.setNext(next);
		
		if (next != null) {
			next.setPrevious(newRoot);
			
			while (next.getNext() != null) {
				next = next.getNext();
			}
			
			path.tail = next;
		}
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
	
	@Override
	public Object clone() {
		AccessPath path = new AccessPath();
		
		path.root = (AccessPathRootElement) root.clone();
		path.tail = path.root;
		
		AccessPathElement next = path.root;
		
		while (next.getNext() != null) {
			path.tail = next;
			next = next.getNext();
		}
		
		return path;
	}

}