package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Attributes are used to supply additional information to instructions about their operands
 * Instances stored as attributes in JPF slots
 *
 * Numeric abstractions use the AbstractValue
 *
 * Predicate abstraction uses the Expression for symbolic execution purposes
 */
public class Attribute {
    private AbstractValue abs;
    private Expression expr;

    public Attribute(AbstractValue abs, Expression expr) {
        this.abs = abs;
        this.expr = expr;
    }

    private AbstractValue getAbstractValue() {
        return abs;
    }
    private Expression getExpression() {
        return expr;
    }
    private void setAbstractValue(AbstractValue abstractValue) {
        abs = abstractValue;
    }
    private void setExpression(Expression expression) {
        expr = expression;
    }

    public static Attribute getAttribute(Object attr) {
        return (Attribute)attr;
    }

    public static void setExpression(Object attr, Expression expr) {
        getAttribute(attr).setExpression(expr);
    }

    public static Expression getExpression(Object attr) {
        if (attr == null) {
            return null;
        }

        return getAttribute(attr).getExpression();
    }

    public static AccessExpression getAccessExpression(Object attr) {
        return (AccessExpression) getExpression(attr);
    }

    public static void setAbstractValue(Object attr, AbstractValue val) {
        getAttribute(attr).setAbstractValue(val);
    }

    public static AbstractValue getAbstractValue(Object attr) {
        if (attr == null) {
            return null;
        }

        return getAttribute(attr).getAbstractValue();
    }

    @Override
    public String toString() {
        return getExpression() == null ? "_" : "@(" + getExpression() + ")";
    }
}
