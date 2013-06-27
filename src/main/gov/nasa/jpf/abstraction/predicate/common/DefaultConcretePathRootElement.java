package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ClassInfo;

public class DefaultConcretePathRootElement extends DefaultAccessPathRootElement implements ConcretePathRootElement {

	private ClassInfo rootClass;
	private ConcretePath.Type type;

	public DefaultConcretePathRootElement(String name, ClassInfo rootClass, ConcretePath.Type type) {
		super(name);

		this.rootClass = rootClass;
		this.type = type;
	}

	@Override
	public ConcretePath.Type getType() {
		return type;
	}

	@Override
	public ClassInfo getClassInfo() {
		return rootClass;
	}

}
