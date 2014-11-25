package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.Verify;

public class LazyRefinementTest extends BaseTest {
    public LazyRefinementTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.branch.prune_infeasible=true");
        config.add("+panda.branch.nondet_force_feasible=true");
        config.add("+classpath+=;lib/jpf-core/build/jpf.jar");
    }

    @Test
    public static void test() {
        createChoices();

        error();
    }

    native private static void createChoices();
    native private static void error();
}
