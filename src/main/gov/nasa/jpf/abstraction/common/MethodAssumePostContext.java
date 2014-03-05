package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

import java.util.List;

public class MethodAssumePostContext extends AbstractMethodContext implements AssumeContext {

	public MethodAssumePostContext(Method method, List<Predicate> predicates) {
		super(method, predicates);
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

}
