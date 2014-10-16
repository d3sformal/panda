package gov.nasa.jpf.abstraction;

public class FindGreaterNonDetTest extends BaseTest {
    public FindGreaterNonDetTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.branch.adjust_concrete_values=false");
        config.add("+panda.branch.prune_infeasible=true");
    }

    @Test
    public static void test() {
        int[] data = new int[5];
        int x = Verifier.unknownInt();

        loadRandomValues(data);

        int pos = findGreater(data, x);

        assert pos == data.length || data[pos] > x;
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
