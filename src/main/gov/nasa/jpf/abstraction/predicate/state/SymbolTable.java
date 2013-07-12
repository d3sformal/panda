package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;

import java.util.Set;

public interface SymbolTable {
	
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix);
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID number);
	public CompleteVariableID resolvePath(AccessPath path);
	
	public Set<AccessPath> processLoad(AccessPath path, CompleteVariableID number);
	public Set<AccessPath> processStore(ConcretePath from, ConcretePath to);
	
}
