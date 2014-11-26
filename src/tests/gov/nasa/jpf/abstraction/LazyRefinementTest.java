package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.Verify;

import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.assertRevisitedAtLeast;
import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.assertVisitedAtMost;

public class LazyRefinementTest extends BaseTest {
    public LazyRefinementTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.branch.prune_infeasible=true");
        config.add("+panda.branch.nondet_force_feasible=true");
        config.add("+search.multiple_errors=true");
        config.add("+classpath+=;lib/jpf-core/build/jpf.jar");
    }

    private final static int choices = 1000;

    @Test
    public static void test1() {
        createChoices(choices);

        error(choices - 1);

        assertRevisitedAtLeast(choices);
        assertVisitedAtMost(choices + 1);
    }

    @Test
    @Config(items = {
        "+panda.refinement.keep_explored_branches=false"
    })
    public static void test2() {
        createChoices(choices);

        error(choices - 1);

        assertRevisitedAtLeast(2 * choices - 1);
        assertVisitedAtMost(2 * choices);
    }

    native private static void createChoices(int choices);
    native private static void error(int choice);
}
