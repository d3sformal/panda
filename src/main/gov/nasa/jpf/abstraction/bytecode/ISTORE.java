package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.AbstractInstructionFactory;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ISTORE extends gov.nasa.jpf.jvm.bytecode.ISTORE {

	public ISTORE(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		LocalVarInfo var = getLocalVarInfo();		
        Attribute source = (Attribute) sf.getOperandAttr(0);
        
        if (source == null) source = new EmptyAttribute();

		Instruction actualNextInsn = super.execute(ti);
        
		ConcretePath from = null;
		ConcretePath to = null;
		
		if (source.getExpression() instanceof ConcretePath) {
			from = (ConcretePath) source.getExpression();
		}
		if (var != null) {
			to = new ConcretePath(getLocalVariableName(), ti, var, ConcretePath.Type.LOCAL);
		}
		
		sf = ti.getModifiableTopFrame();
		sf.setLocalAttr(getLocalVariableIndex(), source);

		AbstractInstructionFactory.abs.processStore(from, to);
		
		return actualNextInsn;
	}
}
