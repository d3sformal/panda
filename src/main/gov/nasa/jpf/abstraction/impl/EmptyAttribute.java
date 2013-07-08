package gov.nasa.jpf.abstraction.impl;

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.predicate.common.Expression;

public class EmptyAttribute implements Attribute {

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
