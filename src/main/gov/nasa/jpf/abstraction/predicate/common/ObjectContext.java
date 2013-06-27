package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public class ObjectContext extends Context {

	public ObjectContext(AccessPath path, List<Predicate> predicates) {
		super(path, predicates);
	}
	
	@Override
	public String toString() {
		String ret = "[object " + path.toString(AccessPath.NotationPolicy.DOT_NOTATION) + "]\n";
		
		ret += super.toString();
		
		return ret;
	}	
}
