package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.predicate.common.Expression;

public class Attribute {
	public AbstractValue abstractValue;
	public Expression expression;
	
	public Attribute(AbstractValue abstractValue, Expression expression) {
		this.abstractValue = abstractValue;
		this.expression = expression;
	}
}
