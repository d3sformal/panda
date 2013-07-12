package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Expression;

public interface Attribute {
	public AbstractValue getAbstractValue();
	public Expression getExpression();
	public void setAbstractValue(AbstractValue abstractValue);
	public void setExpression(Expression expression);
}
