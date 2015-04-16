package gov.nasa.jpf.abstraction;

import static gov.nasa.jpf.abstraction.Verifier.unknownInt;

public class FindGreaterTest extends BaseTest {
    public FindGreaterTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.refinement.method_global=true");
        config.add("+panda.abstract_domain=PREDICATES"); // Disable loading of predicate file
        config.add("+panda.refinement.trace.initialize_array_elements=false");
        config.add("+report.console.property_violation=error,snapshot,trace");
    }

    public static void test(int n) {
        int[] data = new int[5];

        loadRandomValues(data);

        int pos = findGreater(data, n);

        assert pos == data.length || data[pos] > n;
    }

    @Test
    public static void test1() {
        test(10);
    }

    @Test
    public static void test2() {
        int[] data = new int[5];

        int pos = data.length;

        for (int i = 0; i < data.length; ++i) {
            if (data[i] > 10) {
                pos = i;
                break;
            }
        }

        assert pos == data.length || data[pos] > 10;
    }

    @Test
    @Config(items = {
        "+panda.smt.interpolation=SMTInterpol"
    })
    public static void test3() {
        int[] data = new int[5];

        assert data.length > 0;

        int pos = data.length;

        for (int i = 0; i < data.length; ++i) {
            assert i >= 0 && i < data.length;

            if (data[i] > 10) {
                pos = i;
                break;
            }

            assert data[i] < 11;
        }

        assert pos == data.length || data[pos] > 10;
    }

    @Test
    @Config(items = {
        "+panda.branch.adjust_concrete_values=false",
        "+panda.branch.prune_infeasible=true"
    })
    public static void test4() {
        test(unknownInt());
    }

    @Test
    @Config(items = {
        "+panda.refinement.trace.initialize_array_elements=true"
    })
    public static void test5() {
        int[] data = new int[5];

        for (int i = 0; i < data.length; ++i) {
            data[i] = unknownInt();
        }

        int pos = data.length;

        for (int i = 0; i < data.length; ++i) {
            if (data[i] > 10) {
                pos = i;
                break;
            }
        }

        assert pos == data.length || data[pos] > 10;
    }

    private static void loadRandomValues(int[] a) {
        /* havoc(a) */
    }

    private static int findGreater(int[] a, int t) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i] > t) {
                return i;
            }
        }

        return a.length;
    }
}
