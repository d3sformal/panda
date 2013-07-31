package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public interface AbstractBranching {

	public Instruction executeConcrete(ThreadInfo ti);

	public Instruction getSelf();
	
	public Predicate createPredicate(Expression expr1, Expression expr2);
	public AbstractBoolean getCondition(int v1, AbstractValue abs_v1, int v2, AbstractValue abs_v2);
	
	public Instruction getTarget();
	public Instruction getNext(ThreadInfo ti);

}
