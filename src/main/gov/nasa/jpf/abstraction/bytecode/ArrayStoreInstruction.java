package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public interface ArrayStoreInstruction {
    public Instruction execute(ThreadInfo ti);
    public Instruction executeConcrete(ThreadInfo ti);
    public AccessExpression getLHSAccessExpression(StackFrame sf);
    public ElementInfo getLHSArray(StackFrame sf);
    public Expression getIndexExpression(StackFrame sf);
    public Expression getRHSExpression(StackFrame sf);
    public ArrayElementInstruction getSelf();
}
