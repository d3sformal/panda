package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ClassInfo;

public class ConcretePath extends AccessPath {
	
	public enum Type {
		LOCAL,
		HEAP,
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
		/**
		 * If the path is of a primitive form (one element)
		 * it necessarily refers to a primitive local variable.
		 */
		if (root == tail && type == Type.LOCAL) {
			return new LocalNumber(); //local var index
		}
		
		ConcretePathElement element = (ConcretePathElement) tail;
		ClassInfo classInfo = element.getClassInfo();
		
		if (classInfo == null) return null;
		if (!classInfo.isPrimitive()) return null;
		
		/**
		 * If the path is composed of two elements with
		 * root being a class and tail being of a primitive type
		 * then it is a primitive static field.
		 */
		if (root.getNext() == tail && type == Type.STATIC) {
			return new StaticNumber(); //class + static field index
		}
		
		
		/**
		 * Otherwise the closest enclosing object is somewhere
		 * on the heap, regardless of the root type (LOCAL, STATIC).
		 */
		return new HeapNumber(); //tail.previous.objref + field index
	}

}
