package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

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
	
	public AccessPath getObject() {
		return object;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}	
}
