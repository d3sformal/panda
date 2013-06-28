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
	public Object getObject(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		Object object = previous.getObject(ti);
		
		if (object instanceof ElementInfo) {
				ElementInfo ei = (ElementInfo) object;
				
				if (ei.getClassInfo().isArray()) {
					if (ei.getClassInfo().isReferenceArray()) {
						return ti.getElementInfo(ei.getArrayFields().getReferenceValue(index));
					}
					
					return new ArrayElementID(ei.getObjectRef(), index);
				}
		}
		
		return null;
	}

}
