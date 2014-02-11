package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Stores a value from a stack to a local variable and informs abstractions about such an event
 */
public class DSTORE extends gov.nasa.jpf.jvm.bytecode.DSTORE {

	public DSTORE(int index) {
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

        /**
         * Remember what has been stored here
         */
		sf.setLocalAttr(getLocalVariableIndex(), source);

        /**
         * Inform the abstractions that a primitive value of a local variable may have changed
         */
		GlobalAbstraction.getInstance().processPrimitiveStore(from, to);
		
		return actualNextInsn;
	}
}
