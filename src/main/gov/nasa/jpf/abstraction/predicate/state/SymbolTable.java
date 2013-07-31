package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.concrete.VariableID;

import java.util.Set;

public interface SymbolTable {
	
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix);
	public Set<AccessPath> lookupEquivalentAccessPaths(VariableID var);
	public Set<AccessPath> lookupEquivalentAccessPaths(AccessPath path);
	
	public void processLoad(ConcretePath from);
	public Set<AccessPath> processPrimitiveStore(ConcretePath to);
	public Set<AccessPath> processObjectStore(Expression from, ConcretePath to);
	
}
