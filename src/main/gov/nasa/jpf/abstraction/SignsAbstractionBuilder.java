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
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionContext;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class SignsAbstractionBuilder extends PredicateAbstractionBuilder {
    @Override
    public Predicates build(PredicatesParser parser) {
        Predicates predicates = new Predicates();

        for (ExpressionContext exprContext : parser.expressions().val.contexts) {
            PredicateContext predContext = exprContext.getPredicateContextOfProperType();

            for (Expression expr : exprContext.expressions) {
                predContext.put(LessThan.create(expr, Constant.create(0)), TruthValue.UNKNOWN);
                predContext.put(LessThan.create(Constant.create(0), expr), TruthValue.UNKNOWN);

                // This predicate is not necessary and may slow the evaluation process
                //
                // Value of e = 0:
                //
                //                e < 0: true   e < 0: false   e < 0: unknown
                // e > 0: true     inconsist       false          inconsist
                // e > 0: false      false         true           unknown (otherwise e < 0 could not be unknown)
                // e > 0: unknown  inconsist      unknown         unknown (otherwise e < 0 could not be unknown)
                //
                predContext.put(Equals.create(expr, Constant.create(0)), TruthValue.UNKNOWN);
            }

            predicates.contexts.add(predContext);
        }

        return predicates;
    }
}
