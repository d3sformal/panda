package gov.nasa.jpf.abstraction.predicate.common;

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

	@Override
	public String toString() {
		String ret = "[method " + method.toString(AccessPath.NotationPolicy.DOT_NOTATION) + "]\n";
		
		ret = super.toString();
		
		return ret;
	}
}
