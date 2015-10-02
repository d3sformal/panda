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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class PredicateTest {
    /**
     * Test of basic operations
     */

    @Test
    public void testPredicates() {
        Notation.policy = Notation.DOT_NOTATION;

        Predicate p = PredicatesFactory.createPredicateFromString("a + b = 2");

        AccessExpression a = PredicatesFactory.createAccessExpressionFromString("a");
        Expression aplusb = PredicatesFactory.createExpressionFromString("a + b");
        AccessExpression b = PredicatesFactory.createAccessExpressionFromString("b");
        AccessExpression c = PredicatesFactory.createAccessExpressionFromString("c");

        p = p.replace(a, aplusb);
        p = p.replace(b, c);

        Predicate q = PredicatesFactory.createPredicateFromString("(a + c) + c = 2");

        assertEquals(p, q);
    }
}
