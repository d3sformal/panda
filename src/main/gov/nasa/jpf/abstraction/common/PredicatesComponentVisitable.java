package gov.nasa.jpf.abstraction.common;

/**
 * A unifying interface for all the elements of hierarchy:
 * 
 * predicates
 *   -> context
 *     -> predicate
 *       -> expression
 *       
 * to allow a traversal by a visitor.
 */
public interface PredicatesComponentVisitable {
	public void accept(PredicatesComponentVisitor visitor);
}
