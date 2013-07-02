package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcretePathIndexElement extends DefaultAccessPathIndexElement implements ConcretePathIndexElement {
	
	private int index;
	
	public DefaultConcretePathIndexElement(int index) {
		super(new Constant(index));
		
		this.index = index;
	}
	
	@Override
	public ConcretePathElement getPrevious() {
		return (ConcretePathElement)super.getPrevious();
	}

	@Override
	public VariableID getVariableID(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		VariableID var = previous.getVariableID(ti);
		
		if (var instanceof PartialVariableID) {
				ElementInfo ei = ((PartialVariableID)var).getInfo();
				
				if (ei.getClassInfo().isArray()) {
					if (ei.getClassInfo().isReferenceArray()) {
						return new PartialVariableID(ti.getElementInfo(ei.getArrayFields().getReferenceValue(index)));
					}
					
					return new ArrayElementID(ei.getObjectRef(), index);
				}
		}
		
		return null;
	}
	
	@Override
	public Object clone() {
		DefaultConcretePathIndexElement clone = new DefaultConcretePathIndexElement(index);
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}
