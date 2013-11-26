package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

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