package gov.nasa.jpf.abstraction.predicate.statematch;

import gov.nasa.jpf.abstraction.predicate.FailingBaseTest;

public class MatchPrimitiveVarsTest extends FailingBaseTest {
    public static void main(String[] args) {
        boolean b = true, c = true;
        int n;

        if (b) {
            n = 1;
        } else {
            n = 0;

            b = true;
        }

        if (c); // Force state-matching

        int[] array = new int[n];

        array[0] = 42;
    }
}