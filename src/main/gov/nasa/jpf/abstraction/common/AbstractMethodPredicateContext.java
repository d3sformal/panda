package gov.nasa.jpf.abstraction.common;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.Method;

public abstract class AbstractMethodPredicateContext extends PredicateContext {

    private Method method;

    public AbstractMethodPredicateContext(Method method, List<Predicate> predicates) {
        super(predicates);

        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}
