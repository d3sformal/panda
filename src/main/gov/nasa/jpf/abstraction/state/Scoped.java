package gov.nasa.jpf.abstraction.state;

import java.util.Map;

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * An interface for all structures whose behaviour or data change depending on the current runtime method scope.
 */
public interface Scoped {
    public Scope createDefaultScope(ThreadInfo threadInfo, MethodInfo method);
    public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after);
    public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after);
    public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after);
    public void restore(Map<Integer, ? extends Scopes> scopes);
    public Map<Integer, ? extends Scopes> memorize();
    public int count();
    public int depth();
    public Scope get(int depth);
    public void print();
    public void addThread(ThreadInfo threadInfo);
    public void scheduleThread(ThreadInfo threadInfo);
    public void scheduleThread(int threadID);
}
