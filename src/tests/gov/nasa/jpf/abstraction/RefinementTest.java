package gov.nasa.jpf.abstraction;

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

    private static int f(int x) {
        return x + 1;
    }

    private static int g(int x) {
        return 3;
    }

    static class D {
        int celsius = -273;
    }
}
