package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

import java.util.List;

public abstract class Predicate implements PredicatesVisitable {
	public abstract List<AccessPath> getPaths();
	public abstract Predicate replace(AccessPath formerPath, Expression expression);
	
    public String toString() {
    	return toString(AccessPath.policy);
    }
    public String toString(AccessPath.NotationPolicy policy) {
    	PredicatesStringifier stringifier = AccessPath.getStringifier(policy);
		
		accept(stringifier);
		
		return stringifier.getString();
	}
}
