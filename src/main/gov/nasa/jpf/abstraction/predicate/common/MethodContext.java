package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.List;

/**
 * Corresponds to one method section in the input file
 * 
 * [method ...]
 * b = a - 1
 * a * b = 6
 * ...
 * 
 * <<< SOME OTHER SECTION OR EOF
 */
public class MethodContext extends Context {
	
	private Method method;

	public MethodContext(Method method, List<Predicate> predicates) {
		super(predicates);
		
		this.method = method;
	}
	
	public Method getMethod() {
		return method;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

}
