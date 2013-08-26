package gov.nasa.jpf.abstraction.concrete.access;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;

public interface ConcreteAccessExpression extends AccessExpression {
	public Value resolve();
}
