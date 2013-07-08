package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.predicate.common.impl.DefaultAccessPathIndexElement;
import gov.nasa.jpf.abstraction.predicate.common.impl.DefaultAccessPathRootElement;
import gov.nasa.jpf.abstraction.predicate.common.impl.DefaultAccessPathSubElement;

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
	
	public AccessPathRootElement getRoot() {
		return root;
	}
	
	public AccessPathElement getTail() {
		return tail;
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
	
	public static void reRoot(AccessPath path, AccessPath oldPrefix, AccessPath newPrefix) {
		AccessPathElement oldPrefixElement = oldPrefix.root;
		AccessPathElement pathElement = path.root;
		
		while (oldPrefixElement != null && pathElement != null && oldPrefixElement.equals(pathElement)) {
			oldPrefixElement = oldPrefixElement.getNext();
			pathElement = pathElement.getNext();
		}

		AccessPathMiddleElement next = (AccessPathMiddleElement) pathElement;

		path.root = newPrefix.root;

		newPrefix.tail.setNext(next);

		if (next != null) {
			next.setPrevious(newPrefix.tail);
		}
	}
	
	public boolean isPrefix(AccessPath path) {
		AccessPathElement prefixElement = root;
		AccessPathElement pathElement = path.root;
		
		while (prefixElement != null && pathElement != null) {
            if (!prefixElement.equals(pathElement)) {
                return false;
            }

			prefixElement = prefixElement.getNext();
			pathElement = pathElement.getNext();
		}
		
		return prefixElement == null || pathElement != null;
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
		return toString(policy);
	}
	
	@Override
	public String toString(NotationPolicy policy) {
		return root.toString(policy);
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
	public AccessPath clone() {
		AccessPath path = new AccessPath();
		
		path.root = root.clone();
		path.tail = path.root;
		
		AccessPathElement next = path.root;
		
		while (next != null) {
			path.tail = next;
			next = next.getNext();
		}
		
		return path;
	}

}