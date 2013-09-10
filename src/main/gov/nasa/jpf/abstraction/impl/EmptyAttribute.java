package gov.nasa.jpf.abstraction.impl;

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.Expression;

/**
 * Special implementation of an attribute. For those circumstances when there is no better alternative.
 */
public class EmptyAttribute extends Attribute {

	@Override
	public AbstractValue getAbstractValue() {
		return null;
	}

	@Override
	public Expression getExpression() {
		return null;
	}

    @Override
    public void setAbstractValue(AbstractValue abstractValue) {
    }

    @Override
    public void setExpression(Expression expression) {
    }

}
