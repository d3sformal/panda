package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public interface ArrayLoadInstruction {
    public Instruction executeConcrete(ThreadInfo ti);
    public void pushConcrete(StackFrame sf, ElementInfo ei, int index);
    public ArrayElementInstruction getSelf();
}
