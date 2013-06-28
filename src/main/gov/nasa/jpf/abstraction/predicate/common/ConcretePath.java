package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ThreadInfo;

public class ConcretePath extends AccessPath {
	
	public enum Type {
		LOCAL,
		HEAP,
		STATIC
	}
	
	public Type type;
	public Object rootObject;
	public ThreadInfo ti;

	public ConcretePath(String name, ThreadInfo ti, Object rootObject, Type type) {
		this.rootObject = rootObject;
		this.type = type;
		this.ti = ti;
		
		root = createRootElement(name);
		tail = root;
	}
	
	@Override
	protected AccessPathRootElement createRootElement(String name) {
		return new DefaultConcretePathRootElement(name, rootObject, type);
	}
	
	@Override
	public void appendSubElement(String name) {
		appendElement(new DefaultConcretePathSubElement(name));
	}
	
	@Override
	public void appendIndexElement(Expression index) {
		throw new RuntimeException("Concrete path cannot cope with expressions.");
	}
	
	public void appendIndexElement(int index) {
		appendElement(new DefaultConcretePathIndexElement(index));
	}
	
	public VariableID resolve() {
		ConcretePathElement element = (ConcretePathElement) tail;
		Object object = element.getObject(ti);
		
		if (object instanceof VariableID) {
			return (VariableID) object;
		}
		
		return null;
	}

}
