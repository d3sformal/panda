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

import gov.nasa.jpf.vm.Verify;

import gov.nasa.jpf.abstraction.Test;

public class MatchHeapTest extends StateMatchingTest {

    private static Object getX() {
        boolean b = true; // UNTRACKED

        // non-deterministic choice because there is no predicate about variable 'b'
        if (b) {
            return new Object(); // ADDS 1 TRACE
        } else {
            return new Object(); // ADDS 1 TRACE
        }
    }

    private static Object getY() {
        boolean b = true; // UNTRACKED

        // non-deterministic choice because there is no predicate about variable 'b'
        if (b) {
            new Object(); // NO SIDE EFFECTS
        }

        return new Object();
    }

    private static boolean getZ() {
        return true; // UNTRACKED RETURN
    }

    @Test
    private static void scenario1() {
        Object x = getX();

        Verify.breakTransition("Force state-matching");
        // <--- MATCHED
        // equivalent objects allocated on both execution paths
        assertVisitedAtMost(1);

        wasteTime();
    }

    @Test
    private static void scenario2() {
        Object y = getY();

        Verify.breakTransition("Force state-matching");
        // <--- MATCHED
        // equivalent objects returned on both execution paths
        // ignored unreachable objects (see the code of getY)
        assertVisitedAtMost(1);

        wasteTime();
    }

    @Test
    private static void scenario3() {
        Object o;

        while (getZ()) {
            o = new Object(); // NO SIDE EFFECTS

            // <--- MATCHED
            // a single reachable heap object at the end of loop iteration that matches object reachable from 'o' in the previous loop iteration
            // each assignment to 'o' makes the previous value (object) unreachable
            assertNumberOfPossibleValues("o", 1);
            assertVisitedAtMost(2);
            assertRevisitedAtLeast(1);
        }
    }

    private static void wasteTime() {
    }

}
