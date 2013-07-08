package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

/**
 * Corresponds to one object section in the input file
 * 
 * [object ...]
 * b = a - 1
 * a * b = 6
 * ...
 * 
 * <<< SOME OTHER SECTION OR EOF
 */
public class ObjectContext extends Context {
	
	private AccessPath object;

	public ObjectContext(AccessPath path, List<Predicate> predicates) {
		super(predicates);
		
		object = path;
	}
	
	@Override
	public String toString() {
		String ret = "[object " + object.toString(AccessPath.NotationPolicy.DOT_NOTATION) + "]\n";
		
		ret += super.toString();
		
		return ret;
	}	
}