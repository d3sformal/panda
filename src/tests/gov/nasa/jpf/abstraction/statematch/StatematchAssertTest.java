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
package gov.nasa.jpf.abstraction.statematch;

import gov.nasa.jpf.abstraction.FailingTest;
import gov.nasa.jpf.abstraction.Test;

public class StatematchAssertTest extends StateMatchingTest {
    @Test
    public static void test1() {
        Integer o = 42;

        m(o);

        Integer p = o;

        o = new Integer(1);
        o = new Integer(10000);

        o = p;

        m(o);
    }

    @Test
    public static void test2() {
        Integer[] a = new Integer[] { 2, 3 };

        Integer o = 2;

        m(o);

        o = a[0];

        m(o);
    }

    @FailingTest
    public static void test3() {
        Integer[] a = new Integer[] { 2, 3 };

        Integer o = 2;

        m(o);

        int i = 0;

        o = a[i];

        m(o);
    }

    public static void m(Object o) {
        assertSameAliasingOnEveryVisit("o");
    }
}
