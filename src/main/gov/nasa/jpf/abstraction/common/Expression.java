package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Predicate;

import java.util.Set;
import java.util.Map;

/**
 * Common interface of all symbolic expressions
 * 
 * Notably: constants, access expressions, arithmetic operations
 */
public interface Expression extends PredicatesComponentVisitable, Cloneable {
	/**
	 * Collects all complete access expressions present in this one
	 */
	public void addAccessExpressionsToSet(Set<AccessExpression> out);
	
	/**
	 * Performs substitution of an access expression
	 */
	public Expression replace(Map<AccessExpression, Expression> replacements);
	public Expression replace(AccessExpression original, Expression replacement);
    public String toString(Notation policy);
    public Expression clone();
    
    /**
     * Captures that an access expression has been written to (all relevant freads, areads, ... will take that into account)
     */
    public Expression update(AccessExpression expression, Expression newExpression);
    
    /**
     * Decide whether this expression can be a fresh value (newly allocated object)
     */
	public Predicate getPreconditionForBeingFresh();
}
