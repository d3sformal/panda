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

public class MatchPrimitiveVarsTest extends StateMatchingTest {

    @FailingTest
    public static void test1() {
        boolean c = false;

        for (int n = 1; n >= 0; --n) {
            while (c) {} // Force state-matching

            int[] array = new int[n];

            array[0] = 42;
        }
    }

    @Test
    public static void test2() {
        boolean c = false;

        for (int n = 1; n >= 0; --n) {
            while (c) {} // Force state-matching

            assertRevisitedAtLeast(1);
        }
    }

    @Test
    public static void test3() {
        boolean c = false;

        for (int n = 1; n >= 0; --n) {
            while (c) {} // Force state-matching

            assertVisitedAtMost(1);
        }
    }

}
