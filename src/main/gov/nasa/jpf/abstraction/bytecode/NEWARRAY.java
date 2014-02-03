package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class NEWARRAY extends gov.nasa.jpf.jvm.bytecode.NEWARRAY {

	public NEWARRAY(int typeCode) {
		super(typeCode);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		Attribute attr = (Attribute) sf.getOperandAttr();
		
		attr = Attribute.ensureNotNull(attr);
		
		Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

		Instruction actualNextInsn = super.execute(ti);
		
		if (JPFInstructionAdaptor.testNewArrayInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
			return actualNextInsn;
		}
		
		ElementInfo array = ti.getElementInfo(sf.peek());
		
		sf = ti.getModifiableTopFrame();
		sf.setOperandAttr(new NonEmptyAttribute(null, AnonymousArray.create(new Reference(array, ti), attr.getExpression())));
		
		return actualNextInsn;
	}

}
