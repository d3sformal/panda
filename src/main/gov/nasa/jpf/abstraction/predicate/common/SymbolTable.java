package gov.nasa.jpf.abstraction.predicate.common;

import java.util.Set;

public interface SymbolTable {
	
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix);
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID number);
	public CompleteVariableID resolvePath(AccessPath path);
	
	public void registerPathToVariable(AccessPath path, CompleteVariableID number);
	
}
