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

import gov.nasa.jpf.abstraction.BaseTest;

public class StateMatchingTest extends BaseTest {
    native public static void assertVisitedAtMost(int times);
    native public static void assertRevisitedAtLeast(int times);

    // Predicates
    native public static void assertSameValuationOnEveryVisit(String... predicates);
    native public static void assertDifferentValuationOnEveryVisit(String... predicates);
    native public static void assertVisitedAtMostWithValuation(int times, String... predicates);
    native public static void assertRevisitedAtLeastWithValuation(int times, String... predicates);

    // Aliasing
    native public static void assertSameAliasingOnEveryVisit(String... accessExpressions);
}
