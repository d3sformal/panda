package gov.nasa.jpf.abstraction;

public class DivergenceTest extends BaseTest {
    @Test
    @Config(items = {
        "+panda.refinement=true",
        "+panda.branch.adjust_concrete_values=false",
        "+panda.branch.prune_infeasible=true",
        "+listener+=,gov.nasa.jpf.listener.ExecTracker"
    })
    public static void test() {
        int x1 = Verifier.unknownInt();
        int x2 = Verifier.unknownInt();

        if (x1 < 0 || x2 < 0) return;
        if (x1 > 2 || x2 > 2) return;

        int d1 = 1;
        int d2 = 1;

        boolean c1 = Verifier.unknownBool();

        while (x1 > 0 && x2 > 0) {
            if (c1) {
                x1 = x1 - d1;
            } else {
                x2 = x2 - d2;
            }

            c1 = Verifier.unknownBool();
        }

        assert x1 == 0 || x2 == 0;
    }
}
