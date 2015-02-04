package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.jvm.bytecode.ArrayElementInstruction;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public interface ArrayStoreInstruction {
    public Instruction execute(ThreadInfo ti);
    public Instruction executeConcrete(ThreadInfo ti);
    public AccessExpression getArrayExpression(StackFrame sf);
    public ElementInfo getArray(StackFrame sf);
    public Expression getIndexExpression(StackFrame sf);
    public int getIndex(StackFrame sf);
    public Expression getSourceExpression(StackFrame sf);
    public ArrayElementInstruction getSelf();
}
