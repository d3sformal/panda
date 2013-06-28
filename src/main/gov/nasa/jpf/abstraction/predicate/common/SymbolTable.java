package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public interface SymbolTable {
	
	public List<AccessPath> lookupEquivalentAccessPaths(VariableID number);
	public VariableID resolvePath(AccessPath path);
	
	public void register(AccessPath path, VariableID number);
	
}
