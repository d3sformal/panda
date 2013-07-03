package gov.nasa.jpf.abstraction.predicate.grammar;

import java.util.List;

public abstract class Predicate {
	public abstract List<AccessPath> getPaths();
	public abstract String toString(AccessPath.NotationPolicy policy);
}
