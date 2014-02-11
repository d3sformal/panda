package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class LSTORE extends gov.nasa.jpf.jvm.bytecode.LSTORE {

	public LSTORE(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
        Attribute source = (Attribute) sf.getOperandAttr(1);
        
        source = Attribute.ensureNotNull(source);

		Instruction actualNextInsn = super.execute(ti);
        
		Expression from = source.getExpression();
		AccessExpression to = DefaultRoot.create(getLocalVariableName(), getLocalVariableIndex());
		
		sf = ti.getModifiableTopFrame();
		sf.setLocalAttr(getLocalVariableIndex(), source);

		GlobalAbstraction.getInstance().processPrimitiveStore(from, to);
		
		return actualNextInsn;
	}
}
