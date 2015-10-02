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
package gov.nasa.jpf.abstraction.smt;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * A container holding information used to infer a truth value of a particular predicate
 *
 * its positive weakest precondition: WP(statement, p) ... e.g. WP(a++, a = 3) = (a + 1 = 3)
 * its negative weakest precondition: WP(statement, not p)
 *
 * all predicates (often transitive closure) that may affect the truth value of the predicate in question by their truth values:
 *
 * p: a = 3
 *
 * determinants:
 *
 * a = b, b > 2, a < c, c = 4
 */
public class PredicateValueDeterminingInfo {
    public Predicate positiveWeakestPrecondition;
    public Predicate negativeWeakestPrecondition;
    public Map<Predicate, TruthValue> determinants;

    public PredicateValueDeterminingInfo(Predicate positiveWeakestPrecondition, Predicate negativeWeakestPrecondition, Map<Predicate, TruthValue> determinants) {
        this.positiveWeakestPrecondition = positiveWeakestPrecondition;
        this.negativeWeakestPrecondition = negativeWeakestPrecondition;
        this.determinants = determinants;
    }
}
