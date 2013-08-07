package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class FSTORE extends gov.nasa.jpf.jvm.bytecode.FSTORE {

	public FSTORE(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		LocalVarInfo var = getLocalVarInfo();		
        Attribute source = (Attribute) sf.getOperandAttr(0);
        
        if (source == null) source = new EmptyAttribute();

		Instruction actualNextInsn = super.execute(ti);
		
		Expression from = source.getExpression();
		ConcreteAccessExpression to = null;
		
		if (var != null) {
			to = ConcreteAccessExpression.createLocalVarPath(getLocalVariableName(), ti, var);
		}
		
		sf = ti.getModifiableTopFrame();
		sf.setLocalAttr(getLocalVariableIndex(), source);

		GlobalAbstraction.getInstance().processPrimitiveStore(from, to);
		
		return actualNextInsn;
	}
}
