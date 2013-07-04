package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;

import java.util.Set;

public interface SymbolTable {
	
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix);
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID number);
	public CompleteVariableID resolvePath(AccessPath path);
	
	public Set<AccessPath> load(AccessPath path, CompleteVariableID number);
	public Set<AccessPath> assign(ConcretePath from, ConcretePath to);
	
}
