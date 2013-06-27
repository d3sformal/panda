package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ClassInfo;

public class ConcretePath extends AccessPath {
	
	public enum Type {
		INSTANCE,
		STATIC
	}
	
	public Type type;
	public ClassInfo rootClass;

	public ConcretePath(String name, ClassInfo rootClass, Type type) {
		this.rootClass = rootClass;
		this.type = type;
		
		root = createRootElement(name);
		tail = root;
	}
	
	@Override
	protected AccessPathRootElement createRootElement(String name) {
		return new DefaultConcretePathRootElement(name, rootClass, type);
	}
	
	@Override
	protected AccessPathMiddleElement createSubElement(String name) {
		return new DefaultConcretePathSubElement(name);
	}
	
	@Override
	protected AccessPathMiddleElement createIndexElement(Expression index) {
		return new DefaultConcretePathIndexElement(index);
	}
	
	public Number resolve() {
		ConcretePathElement element = (ConcretePathElement) tail;
		ClassInfo classInfo = element.getClassInfo();
		
		if (classInfo == null) return null;
		if (!classInfo.isPrimitive()) return null;
		
		switch (type) {
		case INSTANCE:
			return new InstanceNumber();
		case STATIC:
			return new StaticNumber();
		}
		
		return null;
	}

}
