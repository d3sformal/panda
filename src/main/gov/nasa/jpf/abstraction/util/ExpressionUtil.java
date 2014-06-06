package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class ExpressionUtil {
    public static Expression getExpression(Object o) {
        return (Expression) o;
    }
    public static AccessExpression getAccessExpression(Object o) {
        return (AccessExpression) o;
    }
}
