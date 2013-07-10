package gov.nasa.jpf.abstraction.predicate.common;

import java.util.ArrayList;
import java.util.List;

public abstract class Expression implements PredicatesVisitable {
	protected List<AccessPath> paths = new ArrayList<AccessPath>();
	
	public abstract List<AccessPath> getPaths();
	
    public String toString() {
    	return toString(AccessPath.policy);
    }
    public String toString(AccessPath.NotationPolicy policy) {
    	PredicatesStringifier stringifier = AccessPath.getStringifier(policy);
		
		accept(stringifier);
		
		return stringifier.getString();
	}
}