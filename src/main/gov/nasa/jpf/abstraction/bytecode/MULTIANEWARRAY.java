package gov.nasa.jpf.abstraction.bytecode;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class MULTIANEWARRAY extends gov.nasa.jpf.jvm.bytecode.MULTIANEWARRAY {
	
	public MULTIANEWARRAY (String typeName, int dimensions) {
		super(typeName, dimensions);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		List<Attribute> attrs = new LinkedList<Attribute>();
		
		for (int i = getDimensions() - 1; i >= 0 ; --i) {
			Attribute attr = (Attribute) sf.getOperandAttr(i);
			
			if (attr == null) {
				attr = new EmptyAttribute();
			}
			
			attrs.add(attr);
		}
		
		Attribute attr = attrs.get(attrs.size() - 1);
		attrs.remove(attrs.size() - 1);
		
		Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

		Instruction actualNextInsn = super.execute(ti);
		
		if (JPFInstructionAdaptor.testNewArrayInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
			return actualNextInsn;
		}
		
		ElementInfo array = ti.getElementInfo(sf.peek());
        AnonymousArray expression = AnonymousArray.create(new Reference(array), attr.getExpression());
		
        GlobalAbstraction.getInstance().processNewObject(expression);

		sf = ti.getModifiableTopFrame();
		sf.setOperandAttr(new NonEmptyAttribute(null, expression));
		
		// ALL ELEMENTS ARE NULL
		setArrayAttributes(ti, array, attrs);

		return actualNextInsn;
	}

	private void setArrayAttributes(ThreadInfo ti, ElementInfo array, List<Attribute> subList) {
		if (subList.isEmpty()) return;
		
		ArrayFields fields = array.getArrayFields();
		int size = array.arrayLength();
		
		Attribute attr = subList.get(subList.size() - 1);
		
		for (int i = 0; i < size; ++i) {
			fields.addFieldAttr(size, i, attr);
			
			if (array.isReferenceArray()) {
				ElementInfo subArray = ti.getElementInfo(array.getReferenceElement(i));
				
				setArrayAttributes(ti, subArray, subList.subList(1, subList.size()));
			}
		}
		
		//subList.remove(subList.size() - 1);
	}
}
