package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcretePathSubElement extends DefaultAccessPathSubElement implements ConcretePathSubElement {
	
	public DefaultConcretePathSubElement(String name) {
		super(name);
	}

	@Override
	public ConcretePathElement getPrevious() {
		return (ConcretePathElement)super.getPrevious();
	}

	@Override
	public Object getObject(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		Object object = previous.getObject(ti);
		
		if (object == null) return null;
		if (!(object instanceof ElementInfo)) return null;

		ElementInfo ei = (ElementInfo) object;
		
		object = ei.getFieldValueObject(getName());
		
		if (previous instanceof ConcretePathRootElement) {
			ConcretePathRootElement root = (ConcretePathRootElement) previous;
			
			switch (root.getType()) {
			case STATIC:
				if (!(object instanceof ElementInfo)) {
					return new StaticFieldID(ei.getClassInfo().getName(), getName());
				}
			}
		}
		
		if (!(object instanceof ElementInfo)) {
			return new ObjectFieldID(ei.getObjectRef(), getName());
		}
		
		return object;
	}

}
