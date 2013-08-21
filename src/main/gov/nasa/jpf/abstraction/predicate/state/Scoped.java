package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public interface Scoped {
	public Scope createDefaultScope(MethodInfo method);
	public void processMethodCall(ThreadInfo threadInfo, MethodInfo method);
	public void processMethodReturn();
	public void store(Scope scope);
	public void restore(Scopes scopes);
	public Scopes memorize();
	public int count();
}
