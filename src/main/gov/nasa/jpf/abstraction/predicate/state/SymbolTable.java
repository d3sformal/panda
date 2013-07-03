package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;

import java.util.Set;

public interface SymbolTable {
	
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix);
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID number);
	public CompleteVariableID resolvePath(AccessPath path);
	
	public boolean registerPathToVariable(AccessPath path, CompleteVariableID number);
	
}
