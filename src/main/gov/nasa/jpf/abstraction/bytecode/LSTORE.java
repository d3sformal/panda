package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class LSTORE extends gov.nasa.jpf.jvm.bytecode.LSTORE {

	public LSTORE(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		LocalVarInfo var = getLocalVarInfo();		
        Attribute source = (Attribute) sf.getOperandAttr(1);
        
        if (source == null) source = new EmptyAttribute();

		Instruction ret = super.execute(ti);
        
		if (ret != getNext(ti)) return ret;
        
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

		PredicateAbstraction.processStore(from, to, sf);
		
		return ret;
	}
}