/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.state;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

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
    public void reevaluate(int lastPC, int nextPC, AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression);

    public void dropAllPredicatesSharingSymbolsWith(AccessExpression expression);

    /**
     * Evaluate a predicate but do not store its value, just use other predicates to infer the truth value
     */
    public TruthValue evaluatePredicate(int lastPC, Predicate predicate);

    /**
     * The same as evaluatePredicate, only for multiple predicates at once (effective due to a single call to the SMT)
     */
    public Map<Predicate, TruthValue> evaluatePredicates(int lastPC, Set<Predicate> predicates);

    public Integer evaluateExpression(Expression expression);
    public int[] evaluateExpressionInRange(Expression expression, int lowerBound, int upperBound);
}
