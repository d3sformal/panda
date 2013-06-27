package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public interface AbstractBranching {

	public Instruction executeConcrete(ThreadInfo ti);

	public Instruction getSelf();
	
	public AbstractBoolean getCondition(AbstractValue abs_v1, AbstractValue abs_v2);
	
	public Instruction getTarget();
	public Instruction getNext(ThreadInfo ti);

}
