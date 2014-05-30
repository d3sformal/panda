package matching;

public class Matching {

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

        // <--- NOT MATCHED YET, DUE TO `b` VALUATION IN SCOPE
        // <--- BUT MATCHING HEAP, ONLY REACHABLE OBJECTS

        return new Object();
    }

    private static boolean getZ() {
        return false; // UNTRACKED RETURN
    }

    //////////////////////////////// SCENARIOS ////////////////////////////////
    private static void scenario1() {
        Object x = getX();

        // <--- MATCHED
        // equivalent objects allocated on both execution paths

        wasteTime();
    }

    private static void scenario2() {
        Object y = getY();

        // <--- MATCHED
        // equivalent objects returned on both execution paths
        // ignored unreachable objects (see the code of getY)

        wasteTime();
    }

    private static void scenario3() {
        Object o;

        while (getZ()) {
            o = new Object(); // NO SIDE EFFECTS

            // <--- MATCHED
            // a single reachable heap object at the end of loop iteration that matches object reachable from 'o' in the previous loop iteration
            // each assignment to 'o' makes the previous value (object) unreachable
        }
    }

    //////////////////////////////// MAIN ////////////////////////////////
    public static void main(String[] args) {
        scenario1();
        scenario2();
        scenario3();
    }

    //////////////////////////////// HELPER FUNCTIONS ////////////////////////////////
    private static void wasteTime() {
        for (int ever = 10; ever > 0 ; --ever) {}
    }

}
