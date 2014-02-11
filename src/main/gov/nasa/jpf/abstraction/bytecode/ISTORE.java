package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ISTORE extends gov.nasa.jpf.jvm.bytecode.ISTORE {

	public ISTORE(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
        Attribute source = (Attribute) sf.getOperandAttr(0);
        
        source = Attribute.ensureNotNull(source);

		Instruction actualNextInsn = super.execute(ti);
        
		Expression from = source.getExpression();
		AccessExpression to = DefaultRoot.create(getLocalVariableName(), getLocalVariableIndex());

        System.out.println("ISTORE: " + to + " := " + from);

        for (gov.nasa.jpf.abstraction.common.access.Root var : ((gov.nasa.jpf.abstraction.predicate.PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0).getLocalVariables()) {
            System.out.println("\t" + var);
        }
		
		sf = ti.getModifiableTopFrame();
		sf.setLocalAttr(getLocalVariableIndex(), source);

		GlobalAbstraction.getInstance().processPrimitiveStore(from, to);
		
		return actualNextInsn;
	}
}
