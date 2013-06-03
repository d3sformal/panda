package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public interface AbstractBinaryOperator<T> {
	public Instruction executeConcrete(ThreadInfo ti);
	
	public Abstraction getResult(T v1, Abstraction abs_v1, T v2, Abstraction abs_v2);

	public Instruction getSelf();
	public Instruction getNext(ThreadInfo ti);
}
