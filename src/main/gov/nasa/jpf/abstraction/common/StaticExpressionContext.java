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

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

/**
 * Corresponds to one static section in the input file
 *
 * [static]
 * b
 * a
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.grammar (grammar file Predicates.g4)
 */
public class StaticExpressionContext extends ExpressionContext {

    public StaticExpressionContext(List<Expression> expressions) {
        super(expressions);
    }

    @Override
    public StaticPredicateContext getPredicateContextOfProperType() {
        return new StaticPredicateContext(new LinkedList<Predicate>());
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
