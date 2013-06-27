package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ClassInfo;

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
	
	public Number resolve(ClassInfo info, AccessPathType type) {
		AccessPathElement e = root;
		AccessPathType originalType = type;
		
		while (e.next != null && !info.isPrimitive()) {
			if (e.next instanceof AccessPathIndexElement) {
				if (info.isArray()) {
					info = info.getComponentClassInfo(); //TODO: verify
				} else {
					return null;
				}
			} else if (e.next instanceof AccessPathSubElement) {
				AccessPathSubElement field = (AccessPathSubElement) e.next;

				switch (type) {
				case STATIC:
					info = info.getStaticField(field.name).getClassInfo();
					type = AccessPathType.HEAP;
					break;
				case LOCAL:
				case HEAP:
					info = info.getInstanceField(field.name).getClassInfo();
					break;
				default:
					//TODO
				}
			}
			
			e = e.next;
		}
		
		if (info.isPrimitive()) {
			switch (originalType) {
			case STATIC:
				return new StaticNumber();
			case LOCAL:
				return new LocalNumber();
			case HEAP:
				return new HeapNumber();
			}
		}
		
		return null;
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