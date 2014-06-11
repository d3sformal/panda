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
