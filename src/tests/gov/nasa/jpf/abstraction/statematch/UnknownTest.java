package gov.nasa.jpf.abstraction.statematch;

import gov.nasa.jpf.abstraction.Test;
import gov.nasa.jpf.abstraction.Verifier;

public class UnknownTest extends StateMatchingTest {
    public UnknownTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.branch.prune_infeasible=true");
    }

    @Test
    public static void test1() {
        int i = Verifier.unknownInt();

        // Without matching already explored abstract traces
        // 1. Comes with 0
        // 2. Generates unknown = 1 ... to get to now-disabled then-branch
        // 3. Comes with 1
        // 4. Generates unknown = -1 ... to get to now-disabled else-branch
        // 5. Comes with -1
        // 6. Cannot generate more models for i
        if (i == 1) {
        }

        assertRevisitedAtLeast(1);
        assertVisitedAtMost(2);
    }

    @Test
    public static void test2() {
        int i = Verifier.unknownInt();

        // Test with the special value 0 which is the default
        // Use < to avoid one of the branches having only one model
        // Both branches having multiple models -> higher chance Panda might diverge
        if (i < 0) {
        }

        assertRevisitedAtLeast(1);
        assertVisitedAtMost(2);
    }

    @Test
    public static void test3() {
        int i = Verifier.unknownInt(); // Should register choice (starting with for example 0) and mark the constant as NONDET

        int j = i + 2;

        if (i > 498) { // Inconsistent branching will take Trace Formula and derive such a value of NONDET different from previously derived values
            assert j > 500;
        }

        // There will be one concrete path avoiding the body of the IF
        //   lets say its for i: 0
        //
        // There will be exactly one concrete path hitting the body of the IF
        //   lets say its for i: 499

        assertRevisitedAtLeast(1);
        assertVisitedAtMost(2);
    }

    @Test
    public static void test4() {
        int i = Verifier.unknownInt();

        switch (i) {
            case 0:
            case 42:
            case 1024:
            case -5:
            case Integer.MAX_VALUE:
            case -Integer.MAX_VALUE:
                System.out.println(i);
                break;
            default:
                System.out.println("Default: " + i);
                break;
        }

        assertRevisitedAtLeast(6);
        assertVisitedAtMost(7);
    }
};
