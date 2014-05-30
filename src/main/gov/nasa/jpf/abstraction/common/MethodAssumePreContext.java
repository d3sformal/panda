package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

import java.util.List;

public class MethodAssumePreContext extends AbstractMethodContext implements AssumeContext {

    public MethodAssumePreContext(Method method, List<Predicate> predicates) {
        super(method, predicates);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

}
