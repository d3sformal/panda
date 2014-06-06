package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import java.util.Map;
import java.util.Set;

/**
 * Interface of a structure managing valuations of predicates
 */
public interface PredicateValuation {
    public Set<Predicate> getPredicatesInconsistentWith(Predicate predicate, TruthValue value);
    public void force(Predicate predicate, TruthValue value);

    /**
     * Sets (or adds) a valution of a predicate (if scopes are taken into account, only to the current scope)
     */
    public void put(Predicate predicate, TruthValue value);

    /**
     * Same as put but for more values at once
     */
    public void putAll(Map<Predicate, TruthValue> values);

    /**
     * Removes a valuation
     */
    public void remove(Predicate predicate);

    /**
     * Tests whether a predicate is valuated
     */
    public boolean containsKey(Predicate predicate);

    /**
     * Retrieves a valuation of the predicate
     */
    public TruthValue get(Predicate predicate);

    /**
     * Returns set of all predicates (if scopes are taken into account, only those in the current scope)
     */
    public Set<Predicate> getPredicates();

    /**
     * Cause reevaluation of all predicates affected by given paths
     * @param affected the original symbolic value that was written to
     * @param resolvedAffected set of all aliases of affected
     * @param expression the value being written
     */
    public void reevaluate(AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression);

    public void dropAllPredicatesSharingSymbolsWith(AccessExpression expression);

    /**
     * Evaluate a predicate but do not store its value, just use other predicates to infer the truth value
     */
    public TruthValue evaluatePredicate(Predicate predicate);

    /**
     * The same as evaluatePredicate, only for multiple predicates at once (effective due to a single call to the SMT)
     */
    public Map<Predicate, TruthValue> evaluatePredicates(Set<Predicate> predicates);

    public Integer evaluateExpression(Expression expression);
    public int[] evaluateExpressionInRange(Expression expression, int lowerBound, int upperBound);
}
