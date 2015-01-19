package svcomp.arrays;

// Taken from SVCOMP

public class TwoIndicesTest {
    private static final int SIZE = 5;

    public static void test() {
        int[] a = new int[SIZE];
        int[] b = new int[SIZE];

        int i = 1;
        int j = 0;

        while (i < SIZE) {
            a[j] = b[i];

            i = i + 4;
            j = j + 1;
        }

        i = 1;
        j = 0;

        while (i < SIZE) {
            assert a[j] == b[4 * j + 1];

            i = i + 4;
            j = j + 1;
        }
    }
};
