package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ARRAYLENGTH extends gov.nasa.jpf.jvm.bytecode.ARRAYLENGTH {
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		
		Attribute attr = (Attribute) sf.getOperandAttr();
		
		if (attr == null) attr = new EmptyAttribute();
		
		ConcretePath path = null;
		
		if (attr.getExpression() instanceof ConcretePath) {
			path = (ConcretePath) attr.getExpression();
			path.appendSubElement("length");
		}
		
		Instruction ret = super.execute(ti);
		
		sf = ti.getModifiableTopFrame();
		sf.addOperandAttr(new NonEmptyAttribute(null, path));
		
		return ret;
	}
}
