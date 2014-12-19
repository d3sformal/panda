package gov.nasa.jpf.abstraction;

public class CopyTest extends BaseTest {
    public CopyTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.log_smt=true");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener");
    }

    private static final int N = 1;

    private static void copy2() {
        int[] a1 = new int[N];
        int[] a2 = new int[N];
        int[] a3 = new int[N];

        for (int i = 0; i < a1.length; ++i) {
            int n = a1[i];
            a2[i] = n;
        }
        for (int i = 0; i < a2.length; ++i) {
            int n = a2[i];
            a3[i] = n;
        }

        for (int i = 0; i < a1.length; ++i) {
            assert a1[i] == a3[i];
        }
    }

    @Test
    public static void test1() {
        copy2();
    }

    //@Test
    @Config(items = {
        "+panda.branch.adjust_concrete_values=false",
        "+panda.branch.prune_infeasible=true"
    })
    public static void test2() {
        copy2();
    }
}
