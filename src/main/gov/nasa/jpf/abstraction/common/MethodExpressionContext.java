package gov.nasa.jpf.abstraction.common;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.Method;

/**
 * Corresponds to one method section in the input file
 *
 * It is targeted at a concrete method (e.g. [method pkg.subpkg.Class.method])
 *
 * [method ...]
 * b = a - 1
 * a * b = 6
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.predicate.grammar (grammar file Predicates.g4)
 */
public class MethodExpressionContext extends ExpressionContext {

    private Method method;

    public MethodExpressionContext(Method method, List<Expression> expressions) {
        super(expressions);

        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public MethodPredicateContext getPredicateContext() {
        return new MethodPredicateContext(method, new LinkedList<Predicate>());
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

}
