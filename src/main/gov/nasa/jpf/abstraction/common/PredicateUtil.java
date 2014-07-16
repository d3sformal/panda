package gov.nasa.jpf.abstraction.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Comparison;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class PredicateUtil {
    public static boolean determinesExactConcreteValueOfAccessExpression(Predicate predicate, Map<Predicate, TruthValue> valuations) {
        TruthValue valuation = TruthValue.TRUE;

        while (predicate instanceof Negation) {
            predicate = ((Negation) predicate).predicate;

            valuation = TruthValue.neg(valuation);
        }

        if (predicate instanceof Equals && valuations.get(predicate) == valuation) {
            Equals e = (Equals) predicate;

            Expression a = e.a;
            Expression b = e.b;

            if (a instanceof AccessExpression && b instanceof Constant) {
                return true;
            }
            if (a instanceof Constant && b instanceof AccessExpression) {
                return true;
            }
        }

        return false;
    }

    // Assumes predicate to be comparison (possibly negated) of an access expression and a constant
    public static AccessExpression getAccessExpression(Predicate predicate) {
        while (predicate instanceof Negation) {
            predicate = ((Negation) predicate).predicate;
        }

        Comparison c = (Comparison) predicate;

        Expression a = c.a;
        Expression b = c.b;

        if (a instanceof AccessExpression) {
            return (AccessExpression) a;
        } else {
            return (AccessExpression) b;
        }
    }

    public static boolean forbidsExactConcreteValueOfAccessExpression(Predicate predicate, Map<Predicate, TruthValue> valuations) {
        TruthValue valuation = TruthValue.FALSE;

        while (predicate instanceof Negation) {
            predicate = ((Negation) predicate).predicate;

            valuation = TruthValue.neg(valuation);
        }

        // There is no GreaterThan relation
        if ((predicate instanceof Equals && valuations.get(predicate) == valuation) || predicate instanceof LessThan) {
            Comparison c = (Comparison) predicate;

            Expression a = c.a;
            Expression b = c.b;

            if (a instanceof AccessExpression && b instanceof Constant) {
                return true;
            }

            if (a instanceof Constant && b instanceof AccessExpression) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPredicateOverReturn(Predicate predicate) {
        Set<AccessExpression> exprs = new HashSet<AccessExpression>();

        predicate.addAccessExpressionsToSet(exprs);

        for (AccessExpression expr : exprs) {
            if (expr.getRoot() instanceof ReturnValue && ((ReturnValue) expr.getRoot()).isReturnFromCurrentScope()) return true;
        }

        return false;
    }
}
