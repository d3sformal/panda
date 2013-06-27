package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public interface SymbolTable {
	
	public List<AccessPath> lookupEquivalentAccessPaths(Number number);
	public Number resolvePath(AccessPath path);
	
	public void register(AccessPath path, Number number);
	
}
