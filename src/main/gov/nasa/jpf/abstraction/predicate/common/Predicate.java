package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Predicate implements PredicatesVisitable {
	public abstract List<AccessExpression> getPaths();
	public abstract Predicate replace(AccessExpression formerPath, Expression expression);
	
    public String toString() {
    	return toString(Notation.policy);
    }
    public String toString(Notation policy) {
    	return Notation.convertToString(this, policy);
	}
    
    public Set<Predicate> selectDeterminants(Set<Predicate> universe) {
    	Set<Predicate> ret = new HashSet<Predicate>();
    	
    	for (Predicate candidate : universe) {
			List<AccessExpression> candidatePaths = candidate.getPaths();
			
			for (AccessExpression path : getPaths()) {
				for (AccessExpression candidatePath : candidatePaths) {
					if (candidatePath.isSimilarToPrefixOf(path)) {
						ret.add(candidate);
					}
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
	
	public abstract Predicate update(AccessExpression expression, Expression newExpression);
	
	@Override
	public int hashCode() {
		return toString(Notation.DOT_NOTATION).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Predicate) {
			Predicate p = (Predicate) o;

			return toString(Notation.DOT_NOTATION).equals(p.toString(Notation.DOT_NOTATION));
		}
		
		return false;
	}
}
