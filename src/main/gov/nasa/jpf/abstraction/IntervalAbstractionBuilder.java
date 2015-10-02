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
package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionContext;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class IntervalAbstractionBuilder extends PredicateAbstractionBuilder {
    private int min;
    private int max;

    @Override
    public Predicates build(String... args) {
        min = Integer.parseInt(args[1]);
        max = Integer.parseInt(args[2]);

        return build(args[3]);
    }

    @Override
    public Predicates build(PredicatesParser parser) {
        Predicates predicates = new Predicates();

        for (ExpressionContext exprContext : parser.expressions().val.contexts) {
            PredicateContext predContext = exprContext.getPredicateContextOfProperType();

            for (Expression expr : exprContext.expressions) {
                predContext.put(LessThan.create(expr, Constant.create(min)), TruthValue.UNKNOWN);
                predContext.put(LessThan.create(Constant.create(max), expr), TruthValue.UNKNOWN);
            }

            predicates.contexts.add(predContext);
        }

        return predicates;
    }
}
