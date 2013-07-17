package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathRootElement;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathSubElement;
import gov.nasa.jpf.abstraction.common.impl.PredicatesDotStringifier;
import gov.nasa.jpf.abstraction.common.impl.PredicatesFunctionStringifier;

import java.util.ArrayList;
import java.util.List;

public class AccessPath extends Expression implements Cloneable {
	public static enum NotationPolicy {
		DOT_NOTATION,
		FUNCTION_NOTATION
	}
	
	public static NotationPolicy policy = NotationPolicy.FUNCTION_NOTATION;
	
	public static PredicatesStringifier getDefaultStringifier() {
		return getStringifier(policy);
	}
	
	public static PredicatesStringifier getStringifier(NotationPolicy policy) {
		switch (policy) {
		case DOT_NOTATION:
			return new PredicatesDotStringifier();
		case FUNCTION_NOTATION:
			return new PredicatesFunctionStringifier();
		}
		return null;
	}
	
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

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Expression replace(AccessPath formerPath, Expression expression) {
		if (equals(formerPath)) {
			return expression;
		}

		AccessPath path = new AccessPath();
		
		path.root = root.replace(formerPath, expression);
		path.tail = path.root;
		
		AccessPathElement next = path.root;
		
		while (next != null) {
			path.tail = next;
			next = next.getNext();
		}
		
		return path;
	}

	public AccessPath cutTail() {
		AccessPath prefix = clone();
		
		if (prefix.tail instanceof AccessPathMiddleElement) {
			AccessPathMiddleElement tail = (AccessPathMiddleElement) prefix.tail;
			
			prefix.tail = tail.getPrevious();
			prefix.tail.setNext(null);
		}
		
		return prefix;
	}
	
	public boolean similar(AccessPath path) {
		if (!this.root.getName().equals(path.root.getName())) {
			return false;
		}
		
		AccessPathElement e1 = this.root;
		AccessPathElement e2 = path.root;
		
		while (e1 != null && e2 != null) {
			if (e1 instanceof AccessPathSubElement) {
				AccessPathSubElement s1 = (AccessPathSubElement) e1;
				
				if (e2 instanceof AccessPathSubElement) {
					AccessPathSubElement s2 = (AccessPathSubElement) e2;
					
					if (!s1.getName().equals(s2.getName())) {
						return false;
					}
				} else {
					return false;
				}
			}
			if (e1 instanceof AccessPathIndexElement) {
				if (!(e2 instanceof AccessPathIndexElement)) {
					return false;
				}
			}
			
			e1 = e1.getNext();
			e2 = e2.getNext();
		}
		
		return e1 == null && e2 == null;
	}

}
