package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.concrete.VariableID;

import java.util.Map;
import java.util.Set;

public interface SymbolTable extends Iterable<Map.Entry<AccessExpression, Set<VariableID>>> {
	
	public Set<AccessExpression> lookupAccessPaths(AccessExpression prefix);
	public Set<AccessExpression> lookupEquivalentAccessPaths(VariableID var);
	public Set<AccessExpression> lookupEquivalentAccessPaths(AccessExpression path);
	
	public void processLoad(ConcreteAccessExpression from);
	public Set<AccessExpression> processPrimitiveStore(ConcreteAccessExpression to);
	public Set<AccessExpression> processObjectStore(Expression from, ConcreteAccessExpression to);
	
	public boolean isObject(AccessExpression path);
	public boolean isArray(AccessExpression path);
	
	public void setPathToVars(AccessExpression path, Set<VariableID> vars);
	
}
