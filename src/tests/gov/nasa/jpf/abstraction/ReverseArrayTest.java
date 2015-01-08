package gov.nasa.jpf.abstraction;

// Taken from SVCOMP

public class ReverseArrayTest extends BaseTest {
    private static final int N = 5;

    @Test
    @Config(items = {
        "+panda.refinement=true"
    })
    public static void test() {
        int[] a = new int[N];
        int[] b = new int[N];

        for (int i = 0; i < N; ++i) {
            b[i] = a[N - i - 1];
        }

        for (int j = 0; j < N; ++j) {
            assert a[j] == b[N - j - 1];
        }
    }
};
