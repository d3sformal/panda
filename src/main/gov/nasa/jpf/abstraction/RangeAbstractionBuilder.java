package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionContext;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class RangeAbstractionBuilder extends PredicateAbstractionBuilder {
    private int min;
    private int max;

    @Override
    public Predicates build(String... args) {
        min = Integer.parseInt(args[1]);
        max = Integer.parseInt(args[2]);

        return build(args[3]);
    }

    @Override
    public Predicates build(PredicatesParser parser) {
        Predicates predicates = new Predicates();

        for (ExpressionContext exprContext : parser.expressions().val.contexts) {
            PredicateContext predContext = exprContext.getPredicateContextOfProperType();

            for (Expression expr : exprContext.expressions) {
                predContext.put(LessThan.create(expr, Constant.create(min)), TruthValue.UNKNOWN);

                for (int i = min; i < max + 1; ++i) {
                    predContext.put(Equals.create(expr, Constant.create(i)), TruthValue.UNKNOWN);
                }

                predContext.put(LessThan.create(Constant.create(max), expr), TruthValue.UNKNOWN);
            }

            predicates.contexts.add(predContext);
        }

        return predicates;
    }
}
