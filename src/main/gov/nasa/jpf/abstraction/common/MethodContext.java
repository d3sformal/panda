package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.Method;
import java.util.List;

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
public class MethodContext extends AbstractMethodContext {

    public MethodContext(Method method, List<Predicate> predicates) {
        super(method, predicates);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

}
