package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
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
	
	private AccessExpression method;

	public MethodContext(AccessExpression path, List<Predicate> predicates) {
		super(predicates);
		
		method = path;
	}
	
	public AccessExpression getMethod() {
		return method;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

}
