package gov.nasa.jpf.abstraction;

public class FindGreaterFailingTest extends BaseTest {
    @FailingTest
    public static void test() {
        int[] data = new int[5];

        loadRandomValues(data);

        int pos = findGreater(data, 10);

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
