package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.Method;
import java.util.List;

public abstract class AbstractMethodContext extends Context {

    private Method method;

    public AbstractMethodContext(Method method, List<Predicate> predicates) {
        super(predicates);

        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}
