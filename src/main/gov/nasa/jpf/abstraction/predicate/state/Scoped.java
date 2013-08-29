package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public interface Scoped {
	public Scope createDefaultScope(ThreadInfo threadInfo, MethodInfo method);
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after);
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after);
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after);
	public void store(Scope scope);
	public void restore(Scopes scopes);
	public Scopes memorize();
	public int count();
}
