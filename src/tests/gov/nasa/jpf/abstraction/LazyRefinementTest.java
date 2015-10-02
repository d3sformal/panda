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

import gov.nasa.jpf.vm.Verify;

import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.assertRevisitedAtLeast;
import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.assertVisitedAtMost;

public class LazyRefinementTest extends BaseTest {
    public LazyRefinementTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.refinement.method_global=false"); // Needs to be disabled to minimize the risk of backtracking past the choice generator in `createChoices`
        config.add("+search.multiple_errors=true"); // Needs to be enabled for the asserts to work
    }

    private final static int choices = 1000;

    @Test
    public static void test1() {
        createChoices(choices);

        error(choices - 1);

        assertRevisitedAtLeast(choices);
        assertVisitedAtMost(choices + 1);
    }

    @Test
    @Config(items = {
        "+panda.refinement.keep_explored_branches=false"
    })
    public static void test2() {
        createChoices(choices);

        error(choices - 1);

        assertRevisitedAtLeast(2 * choices - 1);
        assertVisitedAtMost(2 * choices);
    }

    native private static void createChoices(int choices);
    native private static void error(int choice);
}
