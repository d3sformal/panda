package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Predicate implements PredicatesVisitable {
	public abstract List<AccessExpression> getPaths();
	public abstract Predicate replace(AccessExpression formerPath, Expression expression);
	
    public String toString() {
    	return toString(AccessExpression.policy);
    }
    public String toString(AccessExpression.NotationPolicy policy) {
    	PredicatesStringifier stringifier = AccessExpression.getStringifier(policy);
		
		accept(stringifier);
		
		return stringifier.getString();
	}
    
    public Set<Predicate> selectDeterminants(Set<Predicate> universe) {
    	Set<Predicate> ret = new HashSet<Predicate>();
    	
    	for (Predicate candidate : universe) {
			List<AccessExpression> candidatePaths = candidate.getPaths();

			for (AccessExpression path : getPaths()) {
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
	
	@Override
	public int hashCode() {
		return toString(AccessExpression.NotationPolicy.DOT_NOTATION).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Predicate) {
			Predicate p = (Predicate) o;

			return toString(AccessExpression.NotationPolicy.DOT_NOTATION).equals(p.toString(AccessExpression.NotationPolicy.DOT_NOTATION));
		}
		
		return false;
	}
}
