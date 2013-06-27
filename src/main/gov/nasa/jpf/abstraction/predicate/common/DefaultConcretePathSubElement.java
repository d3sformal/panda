package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;

public class DefaultConcretePathSubElement extends DefaultAccessPathSubElement implements ConcretePathSubElement {
	
	public DefaultConcretePathSubElement(String name) {
		super(name);
	}

	@Override
	public ConcretePathElement getPrevious() {
		return (ConcretePathElement)super.getPrevious();
	}

	@Override
	public ClassInfo getClassInfo() {
		ConcretePathElement previous = getPrevious();
		ClassInfo classInfo = previous.getClassInfo();
		FieldInfo fieldInfo;
		
		if (previous instanceof ConcretePathRootElement) {
			ConcretePathRootElement root = (ConcretePathRootElement) previous;
			
			if (root.getType() == ConcretePath.Type.STATIC) {
				fieldInfo = classInfo.getStaticField(getName());
				
				if (fieldInfo == null) return null;
				
				return fieldInfo.getTypeClassInfo();
			}
		}

		if (classInfo == null) return null;
		
		fieldInfo = classInfo.getInstanceField(getName());
		
		if (fieldInfo == null) return null;
		
		return fieldInfo.getTypeClassInfo();
	}

}
