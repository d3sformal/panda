package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;

import java.util.ArrayList;
import java.util.List;

public abstract class Junction extends Predicate {
	public Predicate a;
	public Predicate b;
	
	protected Junction(Predicate a, Predicate b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public List<AccessPath> getPaths() {
		List<AccessPath> ret = new ArrayList<AccessPath>();
		
		ret.addAll(a.getPaths());
		ret.addAll(b.getPaths());
		
		return ret;
	}
	
	public static boolean argumentsDefined(Predicate a, Predicate b) {
		return a != null && b != null;
	}
}
