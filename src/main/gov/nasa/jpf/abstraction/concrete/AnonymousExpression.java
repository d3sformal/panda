package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.Expression;

public abstract class AnonymousExpression extends Expression {
	public abstract PartialVariableID generateVariableID();
}
