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

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * Corresponds to one static section in the input file
 *
 * [static]
 * b = a - 1
 * a * b = 6
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.grammar (grammar file Predicates.g4)
 */
public class StaticPredicateContext extends PredicateContext {

    public StaticPredicateContext(List<Predicate> predicates) {
        super(predicates);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public TruthValue put(Predicate p, TruthValue v) {
        if (!(p.getScope() instanceof BytecodeUnlimitedRange)) {
            throw new RuntimeException("Static predicates should not have a specific scope set");
        }

        return super.put(p, v);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof StaticPredicateContext;
    }

}
