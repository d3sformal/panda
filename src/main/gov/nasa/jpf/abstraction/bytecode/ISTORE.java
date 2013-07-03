package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.Set;

public class ISTORE extends gov.nasa.jpf.jvm.bytecode.ISTORE {

	public ISTORE(int index) {
		super(index);
	}
	
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getModifiableTopFrame();
		LocalVarInfo var = getLocalVarInfo();
		
        Attribute source = (Attribute) sf.getOperandAttr(0);
		ConcretePath from = null;
		ConcretePath to = null;
		
		if (source != null) from = source.accessPath;
		if (var != null) {
			to = new ConcretePath(getLocalVariableName(), ti, var, ConcretePath.Type.LOCAL);
		} else {
			System.err.println(getClass().getSimpleName() + " FAIL " + getLocalVariableName());
		}

		Instruction ret = super.execute(ti);

		Set<AccessPath> affected = ScopedSymbolTable.getInstance().assign(from, to);

		ScopedPredicateValuation.getInstance().reevaluate(affected);
		
		return ret;
	}
}