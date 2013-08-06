package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.ObjectExpression;

public abstract class AnonymousExpression extends ObjectExpression {
	public abstract PartialVariableID generateVariableID();
}
