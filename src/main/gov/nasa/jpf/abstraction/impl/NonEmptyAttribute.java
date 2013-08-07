package gov.nasa.jpf.abstraction.impl;

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;

public class NonEmptyAttribute implements Attribute {
	public AbstractValue abstractValue;
	public Expression expression;
	
	public NonEmptyAttribute(AbstractValue abstractValue, Expression expression) {
		this.abstractValue = abstractValue;
		this.expression = expression;
	}

	@Override
	public AbstractValue getAbstractValue() {
		return abstractValue;
	}

	@Override
	public Expression getExpression() {
		if (expression instanceof AccessExpression) {
			return expression.clone();
		}
		
		return expression;
	}

    @Override
    public void setAbstractValue(AbstractValue abstractValue) {
        this.abstractValue = abstractValue;
    }

    @Override
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
