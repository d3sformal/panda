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
package gov.nasa.jpf.abstraction.common;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Common interface of all symbolic expressions
 *
 * Notably: constants, access expressions, arithmetic operations
 */
public interface Expression extends PredicatesComponentVisitable {
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

    /**
     * Captures that an access expression has been written to (all relevant freads, areads, ... will take that into account)
     */
    public Expression update(AccessExpression expression, Expression newExpression);

    /**
     * Decide whether this expression can be a fresh value (newly allocated object)
     */
    public Predicate getPreconditionForBeingFresh();
}
