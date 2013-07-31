package gov.nasa.jpf.abstraction.concrete.impl;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathElement;
import gov.nasa.jpf.abstraction.concrete.ArrayReference;
import gov.nasa.jpf.abstraction.concrete.ConcretePathElement;
import gov.nasa.jpf.abstraction.concrete.ObjectReference;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public abstract class DefaultConcretePathElement extends DefaultAccessPathElement implements ConcretePathElement {

	public static Reference createLocalVarReference(ThreadInfo ti, ElementInfo ei, LocalVarInfo var) {		
		if (ei.isArray()) {
			Attribute attr = (Attribute) ti.getTopFrame().getLocalAttr(var.getSlotIndex());
			
			if (attr == null) attr = new EmptyAttribute();
			
			return new ArrayReference(ei, attr.getExpression());
		} else {
			return new ObjectReference(ei);
		}
	}
	
	public static Reference createObjectFieldReference(ThreadInfo ti, String name, ElementInfo parent) {
		ElementInfo ei = (ElementInfo)parent.getFieldValueObject(name);
		FieldInfo fi = parent.getClassInfo().getInstanceField(name);

		if (ei.isArray()) {
			Attribute attr = (Attribute) parent.getFieldAttr(fi);
			
			if (attr == null) attr = new EmptyAttribute();

			return new ArrayReference(ei, attr.getExpression());
		} else {
			return new ObjectReference(ei);
		}
	}
	
	public static Reference createStaticFieldReference(ThreadInfo ti, String name, ElementInfo parent) {
		ElementInfo ei = (ElementInfo)parent.getFieldValueObject(name);
		FieldInfo fi = parent.getClassInfo().getStaticField(name);

		if (ei.isArray()) {
			Attribute attr = (Attribute) parent.getFieldAttr(fi);
			
			if (attr == null) attr = new EmptyAttribute();

			return new ArrayReference(ei, attr.getExpression());
		} else {
			return new ObjectReference(ei);
		}
	}
	
	public static Reference createArrayElementReference(ThreadInfo ti, int index, ElementInfo parent) {
		ElementInfo ei = ti.getElementInfo(parent.getFields().getReferenceValue(index));

		if (ei.isArray()) {
			Attribute attr = (Attribute) parent.getArrayFields().getFieldAttr(index);
			
			if (attr == null) attr = new EmptyAttribute();

			return new ArrayReference(ei, attr.getExpression());
		} else {
			return new ObjectReference(ei);
		}
	}

}
