package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;

public class AnonymousExpressionTracker {
    public static void notifyPopped(Expression expr) {
        notifyPopped(expr, 0);
    }

    /**
     * Acts in case of JPF popping an anonymous object from the stack
     *
     * If the anonymous object is not duplicated (it is the original reference created by NEW) and make the predicate abstraction forget all artificial predicates about this anonymous object
     */
    public static void notifyPopped(Expression expr, int depth) {
        if (expr instanceof AnonymousExpression) {
            AnonymousExpression anonymous = (AnonymousExpression) expr;

            if (!anonymous.isDuplicate()) {
                PredicateAbstraction.getInstance().getPredicateValuation().get(depth).dropAllPredicatesSharingSymbolsWith((AccessExpression) expr);
            }
        }
    }
}
