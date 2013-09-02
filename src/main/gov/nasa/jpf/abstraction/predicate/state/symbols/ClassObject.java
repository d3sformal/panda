package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

public class ClassObject extends Value {
	private PackageAndClass classObject;
	private StructuredValueSlot slot;
	
	protected ClassObject() {
	}
	
	public ClassObject(PackageAndClass classObject, ClassStatics value) {
		this.classObject = classObject;
		
		slot = new StructuredValueSlot(this, classObject.getName(), value);
	}
	
	public AccessExpression getAccessExpression() {
		return classObject;
	}
	
	public StructuredValueSlot getSlot() {
		return slot;
	}
	
	@Override
	public String toString() {
		return classObject.getName();
	}

	@Override
	public ClassObject cloneInto(Universe universe) {
		ClassObject clone = new ClassObject();
		
		clone.classObject = classObject.clone();
		clone.slot = slot.cloneInto(universe, this);		
		
		return clone;
	}

	@Override
	public ClassObject cloneInto(Universe universe, Slot slot) {
		return cloneInto(universe);
	}
}
