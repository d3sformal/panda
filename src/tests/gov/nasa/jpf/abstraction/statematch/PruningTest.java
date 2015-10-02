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

import gov.nasa.jpf.abstraction.Config;
import gov.nasa.jpf.abstraction.FailingTest;
import gov.nasa.jpf.abstraction.Test;

public class PruningTest extends StateMatchingTest {
    public PruningTest() {
        config.add("+panda.branch.adjust_concrete_values=false");
        config.add("+panda.branch.prune_infeasible=true");
    }

    private static int counter;

    private static boolean cond() {
        counter = counter + 1;

        return counter < 3;
    }

    @FailingTest
    public static void test1() {
        counter = 0;

        while (cond()) {
            // Body
        } // Skip



        // Check reachability of this branch
        assert false;



        // Threat:
        //
        //   0) there is no predicate
        //   1) `while` produces two abstract branches
        //
        //
        //   2) Body -> statematch
        //
        //     or
        //
        //   3) Skip (but this one is spurious and pruned (not enabled in concrete execution))
        //
        // Therefore the else branch is never discovered (even though: (body -> body -> skip) reaches assert)
    }

    // Here we ensure all the iterations of the loop are distinguished (and not matched)
    @FailingTest
    public static void test2() {
        addStaticAbstractionPredicate("class(gov.nasa.jpf.abstraction.statematch.PruningTest).counter = 0");
        addStaticAbstractionPredicate("class(gov.nasa.jpf.abstraction.statematch.PruningTest).counter = 1");
        addStaticAbstractionPredicate("class(gov.nasa.jpf.abstraction.statematch.PruningTest).counter = 2");
        addStaticAbstractionPredicate("class(gov.nasa.jpf.abstraction.statematch.PruningTest).counter = 3");

        test1();
    }
}
