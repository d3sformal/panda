package gov.nasa.jpf.abstraction.predicate.grammar;

import java.util.List;

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
