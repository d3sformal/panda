package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;

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
	
	private AccessPath method;

	public MethodContext(AccessPath path, List<Predicate> predicates) {
		super(predicates);
		
		method = path;
	}
	
	public AccessPath getMethod() {
		return method;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

}
