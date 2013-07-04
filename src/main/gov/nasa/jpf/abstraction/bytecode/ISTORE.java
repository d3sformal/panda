package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
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

		Instruction ret = super.execute(ti);
        
		if (ret != getNext(ti)) return ret;
        
		ConcretePath from = null;
		ConcretePath to = null;
		
		if (source != null) from = source.accessPath;
		if (var != null) {
			to = new ConcretePath(getLocalVariableName(), ti, var, ConcretePath.Type.LOCAL);
		} else {
			System.err.println(getClass().getSimpleName() + " FAIL " + getLocalVariableName());
		}

		PredicateAbstraction.processStore(from, to);
		
		return ret;
	}
}