package gov.nasa.jpf.abstraction.predicate.statematch;

import gov.nasa.jpf.abstraction.predicate.Test;
import gov.nasa.jpf.abstraction.predicate.FailingTest;

public class MatchPrimitiveVarsTest extends StateMatchingTest {

    @FailingTest
    public static void test1() {
        boolean b = true, c = true;
        int n;

        if (b) {
            n = 1;
        } else {
            n = 0;

            b = true;
        }

        while (c) {} // Force state-matching

        int[] array = new int[n];

        array[0] = 42;
    }

    @Test
    public static void test2() {
        boolean b = true, c = true;
        int n;

        if (b) {
            n = 1;
        } else {
            n = 0;

            b = true;
        }

        while (c) {} // Force state-matching

        assertRevisitedAtLeast(1);
    }

    @Test
    public static void test3() {
        boolean b = true, c = true;
        int n;

        if (b) {
            n = 1;
        } else {
            n = 0;

            b = true;
        }

        while (c) {} // Force state-matching

        assertVisitedAtMost(1);
    }

}
