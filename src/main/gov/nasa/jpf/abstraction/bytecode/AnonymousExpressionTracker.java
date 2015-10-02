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
package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;

public class AnonymousExpressionTracker {
    public static void notifyPopped(Expression expr) {
        notifyPopped(expr, 0);
    }

    /**
     * Acts in case of JPF popping an anonymous object from the stack
     *
     * If the anonymous object is not duplicated (it is the original reference created by NEW) and make the predicate abstraction forget all artificial predicates about this anonymous object
     */
    public static void notifyPopped(Expression expr, int depth) {
        if (expr instanceof AnonymousExpression) {
            AnonymousExpression anonymous = (AnonymousExpression) expr;

            if (!anonymous.isDuplicate()) {
                PredicateAbstraction.getInstance().getPredicateValuation().get(depth).dropAllPredicatesSharingSymbolsWith((AccessExpression) expr);
            }
        }
    }
}
