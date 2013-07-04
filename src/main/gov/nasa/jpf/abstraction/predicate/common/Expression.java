package gov.nasa.jpf.abstraction.predicate.common;

import java.util.ArrayList;
import java.util.List;

public abstract class Expression {
	protected List<AccessPath> paths = new ArrayList<AccessPath>();
	
	public abstract List<AccessPath> getPaths();
	public abstract String toString(AccessPath.NotationPolicy policy);
	public String toString() {
		return toString(AccessPath.policy);
	}
}