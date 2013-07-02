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
	public VariableID getVariableID(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		VariableID var = previous.getVariableID(ti);
		
		if (var == null) return null;
		if (var instanceof CompleteVariableID) return null;

		ElementInfo ei = ((PartialVariableID)var).getInfo();
		
		Object object = ei.getFieldValueObject(getName());
		
		if (previous instanceof ConcretePathRootElement) {
			ConcretePathRootElement root = (ConcretePathRootElement) previous;
			
			switch (root.getType()) {
			case STATIC:
				if (!(object instanceof ElementInfo)) {
					return new StaticFieldID(ei.getClassInfo().getName(), getName());
				}
			}
		}
		
		if (object instanceof ElementInfo) {
			return new PartialVariableID((ElementInfo)object);
		}
		
		return new ObjectFieldID(ei.getObjectRef(), getName());
	}
	
	@Override
	public Object clone() {
		DefaultConcretePathSubElement clone = new DefaultConcretePathSubElement(getName());
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}
