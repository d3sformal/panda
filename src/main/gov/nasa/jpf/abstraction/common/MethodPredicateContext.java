package gov.nasa.jpf.abstraction.common;

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
 * @see gov.nasa.jpf.abstraction.grammar (grammar file Predicates.g4)
 */
public class MethodPredicateContext extends AbstractMethodPredicateContext {

    public MethodPredicateContext(Method method, List<Predicate> predicates) {
        super(method, predicates);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            MethodPredicateContext ctx = (MethodPredicateContext) o;

            return getMethod().equals(ctx.getMethod());
        }

        return false;
    }

}
