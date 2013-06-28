package gov.nasa.jpf.abstraction.predicate.common;

import java.util.Set;

public interface SymbolTable {
	
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix);
	public Set<AccessPath> lookupEquivalentAccessPaths(VariableID number);
	public VariableID resolvePath(AccessPath path);
	
	public void register(AccessPath path, VariableID number);
	
}
