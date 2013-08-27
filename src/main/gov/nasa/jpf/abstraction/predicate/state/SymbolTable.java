package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.Set;

public interface SymbolTable {
	public Set<AccessExpression> processPrimitiveStore(AccessExpression to);
	public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to);
	public boolean isArray(AccessExpression path);
	public boolean isObject(AccessExpression path);
	public boolean isPrimitive(AccessExpression path);
}
