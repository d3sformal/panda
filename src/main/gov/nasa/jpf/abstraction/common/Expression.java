package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Predicate;

import java.util.List;
import java.util.Map;

/**
 * Common interface of all symbolic expressions
 * 
 * Notably: constants, access expressions, arithmetic operations
 */
public interface Expression extends PredicatesVisitable, Cloneable {
	/**
	 * Collects all complete access expressions present in this one
	 */
	public List<AccessExpression> getAccessExpressions();
	
	/**
	 * Performs substitution of an access expression
	 */
	public Expression replace(Map<AccessExpression, Expression> replacements);
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
