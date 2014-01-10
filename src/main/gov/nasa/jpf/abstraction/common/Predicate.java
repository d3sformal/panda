package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A common ancestor to all predicates used in the abstraction
 */
public abstract class Predicate implements PredicatesComponentVisitable, BranchingCondition {
	public abstract List<AccessExpression> getPaths();
	public abstract Predicate replace(Map<AccessExpression, Expression> replacements);

    public final Predicate replace(AccessExpression original, Expression replacement) {
        Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

        replacements.put(original, replacement);

        return replace(replacements);
    }

    private Integer hashCodeValue = null;

    public String toString() {
    	return toString(Notation.policy);
    }
    public String toString(Notation policy) {
    	return Notation.convertToString(this, policy);
	}
    
    /**
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may directly determine the value of this predicate
     */
    public Set<Predicate> selectDeterminants(Set<Predicate> universe) {
    	Set<Predicate> ret = new HashSet<Predicate>();
    	
	for (AccessExpression path : getPaths()) {
    		for (Predicate candidate : universe) {
			List<AccessExpression> candidatePaths = candidate.getPaths();

			for (AccessExpression candidatePath : candidatePaths) {
				for (AccessExpression candidateSubPath : candidatePath.getAllPrefixes()) {
					if (candidateSubPath.isSimilarToPrefixOf(path)) {
						ret.add(candidate);
					}
				}
			}
		}
	}
    	
    	return ret;
    }
    
    /**
     * Finds a transitive closure of all predicates that may infer the value of this one.
     * 
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may determine the value of this predicate
     */
	public Set<Predicate> determinantClosure(Set<Predicate> universe) {
		Set<Predicate> cur;
		Set<Predicate> ret = selectDeterminants(universe);
		
		int prevSize = 0;
		
		while (prevSize != ret.size()) {
			prevSize = ret.size();

			cur = new HashSet<Predicate>();

			for (Predicate predicate : ret) {
				cur.addAll(predicate.selectDeterminants(universe));
			}
			
			ret = cur;
		}
		
		return ret;
	}
	
	/**
	 * Changes all access expression present in the predicate to a form which reflects an assignment "expression := newExpression"
	 * 
	 * Let p = (a = 3):
	 *   update(p, a, b + 3) returns b + 3 = 3
	 *   
	 * Let q = (aread(arr, a, 1) = 0):
	 *   update(q, a[0], 3) returns aread(awrite(arr, a, 0, 3), 1) = 0
	 *   
	 * Let r = (fread(f, o) = 10)
	 *   update(r, s.f, x) returns fread(fwrite(f, s, x), o) = 10
	 *   
	 * Used to determine weakest preconditions.
	 * 
	 * @param expression an access expression being written to (e.g. local variable "a")
	 * @param newExpression any arbitrary expression being written (e.g. "b + 3")
	 * @return a predicate reflecting the updates
	 */
	public abstract Predicate update(AccessExpression expression, Expression newExpression);
	
	@Override
	public final int hashCode() {
        if (hashCodeValue == null) {
		    hashCodeValue = toString(Notation.DOT_NOTATION).hashCode();
        }
		return hashCodeValue;
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
