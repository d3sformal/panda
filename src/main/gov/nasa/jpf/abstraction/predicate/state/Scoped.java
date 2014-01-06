package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * An interface for all structures whose behaviour or data change depending on the current runtime method scope.
 */
public interface Scoped {
	public Scope createDefaultScope(ThreadInfo threadInfo, MethodInfo method);
	public SideEffect processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after);
	public SideEffect processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect);
	public SideEffect processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect);
	public void store(Scope scope);
	public void restore(Scopes scopes);
	public Scopes memorize();
	public int count();
    public Scope get(int depth);
}
