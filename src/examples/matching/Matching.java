package matching;

public class Matching {

    static int counter = 3;

    private static Object getX() {
        boolean b = true; // UNTRACKED

        if (b) {
            return new Object(); // ADDS 1 TRACE
        } else {
            return new Object(); // ADDS 1 TRACE
        }
    }

    private static Object getY() {
        boolean b = true; // UNTRACKED

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
        
        wasteTime();
    }

    private static void scenario2() {
        Object y = getY();

        // <--- MATCHED
        
        wasteTime();
    }

    private static void scenario3() {
        Object o;

        while (getZ()) {
            o = new Object(); // NO SIDE EFFECTS

            // <--- MATCHED
        }
    }

    //////////////////////////////// MAIN ////////////////////////////////
    public static void main(String[] args) {
        scenario1();
        scenario2();
        scenario3();
    }

    //////////////////////////////// HELPER FUNCTIONS ////////////////////////////////
    private static boolean ever() {
        return --counter == 0;
    }

    private static void wasteTime() {
        for (; ever() ;) {}
    }

}
