package svcomp.arrays;

// Taken from SVCOMP

public class ReverseArrayTest {
    private static final int N = 5;

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
