package svcomp.arrays;

import static gov.nasa.jpf.abstraction.Verifier.unknownInt;

// Taken from SVCOMP

public class ReverseArray {
    private static final int N = 5;

    public static void test() {
        int[] a = new int[N];
        int[] b = new int[N];

        for (int i = 0; i < N; ++i) {
            a[i] = unknownInt();
        }

        for (int i = 0; i < N; ++i) {
            b[i] = a[N - i - 1];
        }

        for (int j = 0; j < N; ++j) {
            assert a[j] == b[N - j - 1];
        }
    }
};
