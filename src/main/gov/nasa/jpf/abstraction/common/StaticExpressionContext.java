package gov.nasa.jpf.abstraction.common;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

/**
 * Corresponds to one static section in the input file
 *
 * [static]
 * b
 * a
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.predicate.grammar (grammar file Predicates.g4)
 */
public class StaticExpressionContext extends ExpressionContext {

    public StaticExpressionContext(List<Expression> expressions) {
        super(expressions);
    }

    @Override
    public StaticPredicateContext getPredicateContext() {
        return new StaticPredicateContext(new LinkedList<Predicate>());
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
