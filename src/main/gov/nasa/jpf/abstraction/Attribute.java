package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;

/**
 * Attributes are used to supply additional information to instructions about their operands
 * Instances stored as attributes in JPF slots
 *
 * Numeric abstractions use the AbstractValue
 *
 * Predicate abstraction uses the Expression for symbolic execution purposes
 */
public abstract class Attribute {
	public abstract AbstractValue getAbstractValue();
	public abstract Expression getExpression();
	public abstract void setAbstractValue(AbstractValue abstractValue);
	public abstract void setExpression(Expression expression);
	public static Attribute ensureNotNull(Attribute attr) {
		if (attr == null) {
			return new EmptyAttribute();
		}
		
		return attr;
	}
}
