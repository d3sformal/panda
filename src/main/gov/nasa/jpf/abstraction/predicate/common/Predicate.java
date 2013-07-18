package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    
    private Set<Predicate> selectDeterminants(Set<Predicate> universe) {
    	Set<Predicate> ret = new HashSet<Predicate>();
    	
    	for (Predicate candidate : universe) {
			List<AccessPath> candidatePaths = candidate.getPaths();

			for (AccessPath path : getPaths()) {
				if (candidatePaths.contains(path)) {
					ret.add(candidate);
				}
			}
		}
    	
    	return ret;
    }
    
	public Set<Predicate> determinantClosure(Set<Predicate> universe) {
		Set<Predicate> cur;
		Set<Predicate> ret = selectDeterminants(universe);
		
		int formerSize = 0;
		
		while (formerSize != ret.size()) {
			formerSize = ret.size();

			cur = new HashSet<Predicate>();

			for (Predicate predicate : ret) {
				cur.addAll(predicate.selectDeterminants(universe));
			}
			
			ret = cur;
		}
		
		return ret;
	}
}
