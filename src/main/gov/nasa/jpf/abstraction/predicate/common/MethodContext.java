package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public class MethodContext extends Context {

	public MethodContext(AccessPath path, List<Predicate> predicates) {
		super(path, predicates);
	}

	@Override
	public String toString() {
		String ret = "[method " + path.toString(AccessPath.NotationPolicy.DOT_NOTATION) + "]\n";
		
		for (Predicate c : predicates) {
			ret += c.toString() + "\n";
		}
		
		return ret;
	}
}
