package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.vm.MethodInfo;

public interface Scoped {
	public Scope createDefaultScope(MethodInfo method);
	public void processMethodCall(MethodInfo method);
	public void processMethodReturn();
	public void store(Scope scope);
	public void restore(Scope scope);
	public Scope memorize();
	public int count();
}
