package gov.nasa.jpf.abstraction;

// Taken from SVCOMP

public class TwoIndicesTest extends BaseTest {
    private static final int SIZE = 5;

    @Test
    @Config(items = {
        "+panda.refinement=true"
    })
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
