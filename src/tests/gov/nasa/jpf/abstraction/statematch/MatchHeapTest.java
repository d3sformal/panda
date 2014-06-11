package gov.nasa.jpf.abstraction.statematch;

import gov.nasa.jpf.abstraction.Test;
import gov.nasa.jpf.vm.Verify;

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
        return false; // UNTRACKED RETURN
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
