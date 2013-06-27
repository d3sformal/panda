package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ClassInfo;

public class DefaultConcretePathIndexElement extends DefaultAccessPathIndexElement implements ConcretePathIndexElement {
	
	public DefaultConcretePathIndexElement(Expression index) {
		super(index);
	}
	
	@Override
	public ConcretePathElement getPrevious() {
		return (ConcretePathElement)super.getPrevious();
	}

	@Override
	public ClassInfo getClassInfo() {
		ConcretePathElement previous = getPrevious();
		ClassInfo classInfo = previous.getClassInfo();
		
		if (classInfo.isArray()) {
				//TODO!!!
		}
		
		return null;
	}

}
