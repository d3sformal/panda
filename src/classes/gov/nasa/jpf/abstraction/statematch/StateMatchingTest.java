package gov.nasa.jpf.abstraction.statematch;

import gov.nasa.jpf.abstraction.BaseTest;

public class StateMatchingTest extends BaseTest {
    native public static void assertVisitedAtMost(int times);
    native public static void assertRevisitedAtLeast(int times);

    // Predicates
    native public static void assertSameValuationOnEveryVisit(String... predicates);
    native public static void assertDifferentValuationOnEveryVisit(String... predicates);
    native public static void assertVisitedAtMostWithValuation(int times, String... predicates);
    native public static void assertRevisitedAtLeastWithValuation(int times, String... predicates);

    // Aliasing
    native public static void assertSameAliasingOnEveryVisit(String... accessExpressions);
}
