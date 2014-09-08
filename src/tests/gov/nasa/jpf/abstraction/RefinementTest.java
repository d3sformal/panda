package gov.nasa.jpf.abstraction;

import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.*;

public class RefinementTest extends BaseTest {
    public RefinementTest() {
        config.add("+panda.interpolation=true");
    }

    @Test
    public static void test1() {
        int i = 0;

        assert i == 0;
    }

    @Test
    public static void test2() {
        int[] a = new int[1];

        a[0] = 42;

        int i = 0;

        assert a[i] == 42;
    }

    @Test
    public static void test3() {
        D degrees = new D();

        degrees.celsius = -273;

        assert -273 == degrees.celsius;
    }

    // Interpolants over anonymous objects (this = fresh) not yet supported
    //@Test
    public static void test4() {
        D degrees = new D();

        assert -273 == degrees.celsius;
    }

    // Interpolants find return = x + 1, which relies on a local variable and thus will not result in propagation of the predicate past method boundary
    //@Test
    public static void test5() {
        int x = 2;

        assert f(x) == 3;
    }

    @Test
    public static void test6() {
        int x = 0;

        assert g(x) == 3;
    }

    @Test
    public static void test7() {
        assertVisitedAtMost(2); // Ensure second refinement does not backtrack completely

        // Ensure refinement in different methods
        h();
    }

    @Test
    public static void test8() {
        int[] arr = new int[3];

        arr[0] = 0;
        arr[1] = 1;
        arr[2] = 2;

        int i = 0;

        if (i >= 0 && i < arr.length) {
            // choice over arr[?]
            int d = arr[i];

            int e = 0;
            assert e == 0; // unrelated spurious error
        }
    }

    private static int f(int x) {
        return x + 1;
    }

    private static int g(int x) {
        return 3;
    }

    private static void h() {
        int x = 0;

        assert x == 0; // creates new states (1 ok, 1 spurious error)

        // Get here after first refinement

        i();
    }

    private static void i() {
        int x = 0;

        assert x == 0; // creates new states (1 ok, 1 spurious error)

        // Get here after second refinement
    }

    static class D {
        int celsius = -273;
    }
}
