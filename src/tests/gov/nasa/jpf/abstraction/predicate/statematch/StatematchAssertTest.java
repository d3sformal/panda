package gov.nasa.jpf.abstraction.predicate.statematch;

import gov.nasa.jpf.abstraction.predicate.FailingTest;
import gov.nasa.jpf.abstraction.predicate.Test;

public class StatematchAssertTest extends StateMatchingTest {
    @Test
    public static void test1() {
        Integer o = 42;

        m(o);

        Integer p = o;

        o = new Integer(1);
        o = new Integer(10000);

        o = p;

        m(o);
    }

    @Test
    public static void test2() {
        Integer[] a = new Integer[] { 2, 3 };

        Integer o = 2;

        m(o);

        o = a[0];

        m(o);
    }

    @FailingTest
    public static void test3() {
        Integer[] a = new Integer[] { 2, 3 };

        Integer o = 2;

        m(o);

        int i = 0;

        o = a[i];

        m(o);
    }

    public static void m(Object o) {
        assertSameAliasingOnEveryVisit("o");
    }
}
