package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;

public interface ArrayLoadInstruction {
    public Instruction executeConcrete(ThreadInfo ti);
    public void pushConcrete(StackFrame sf, ElementInfo ei, int index);
    public void setAttribute(StackFrame sf, Expression e);
    public ArrayElementInstruction getSelf();
}
