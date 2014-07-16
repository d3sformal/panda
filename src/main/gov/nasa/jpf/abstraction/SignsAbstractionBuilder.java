package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionContext;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;

public class SignsAbstractionBuilder extends PredicateAbstractionBuilder {
    @Override
    public Predicates build(PredicatesParser parser) {
        Predicates predicates = new Predicates();

        for (ExpressionContext exprContext : parser.expressions().val.contexts) {
            PredicateContext predContext = exprContext.getPredicateContextOfProperType();

            for (Expression expr : exprContext.expressions) {
                predContext.predicates.add(LessThan.create(expr, Constant.create(0)));
                predContext.predicates.add(LessThan.create(Constant.create(0), expr));

                // This predicate is not necessary and may slow the evaluation process
                //
                // Value of e = 0:
                //
                //                e < 0: true   e < 0: false   e < 0: unknown
                // e > 0: true     inconsist       false          inconsist
                // e > 0: false      false         true           unknown (otherwise e < 0 could not be unknown)
                // e > 0: unknown  inconsist      unknown         unknown (otherwise e < 0 could not be unknown)
                //
                predContext.predicates.add(Equals.create(expr, Constant.create(0)));
            }

            predicates.contexts.add(predContext);
        }

        return predicates;
    }
}
