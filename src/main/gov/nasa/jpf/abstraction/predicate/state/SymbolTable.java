package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;

import java.util.Set;

public interface SymbolTable {
	public Set<AccessExpression> processPrimitiveStore(ConcreteAccessExpression to);
	public Set<AccessExpression> processObjectStore(Expression from, ConcreteAccessExpression to);
	public boolean isArray(ConcreteAccessExpression path);
	public boolean isObject(ConcreteAccessExpression path);
	public boolean isPrimitive(ConcreteAccessExpression path);
}
