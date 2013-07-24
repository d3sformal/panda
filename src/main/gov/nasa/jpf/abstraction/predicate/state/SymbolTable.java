package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;

import java.util.Set;

public interface SymbolTable {
	
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix);
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID var);
	public Set<AccessPath> lookupEquivalentAccessPaths(AccessPath path);
	public Set<CompleteVariableID> resolvePath(AccessPath path);
	
	public void processLoad(ConcretePath from);
	public Set<AccessPath> processPrimitiveStore(ConcretePath to);
	public Set<AccessPath> processObjectStore(ConcretePath from, ConcretePath to);
	
}
