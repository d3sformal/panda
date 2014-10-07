package gov.nasa.jpf.abstraction;

public class InterpolationTest extends BaseTest {
    public InterpolationTest() {
        config.add("+panda.refinement=true");
    }

    @Test
    public static void test1() {
        int x = 0;
        int y = 0;

        assert x == y;
    }

    @Test
    public static void test2() {
        D d = new D();

        d.val = 10;

        assert d.val == 10;
    }

    @Test
    public static void test3() {
        int[] a = new int[3];

        a[0]= 10;

        assert a[0] == 10;
    }

    static class D {
        int val;
    }
}
