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

import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * A common ancestor to Conjunction and Disjunction (and Implication)
 *
 * @see gov.nasa.jpf.abstraction.common.Conjunction, gov.nasa.jpf.abstraction.common.Disjunction, gov.nasa.jpf.abstraction.common.Implication
 */
public abstract class Formula extends Predicate {
    public Predicate a;
    public Predicate b;

    protected Formula(Predicate a, Predicate b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        a.addAccessExpressionsToSet(out);
        b.addAccessExpressionsToSet(out);
    }

    public static boolean argumentsDefined(Predicate a, Predicate b) {
        return a != null && b != null;
    }
}
