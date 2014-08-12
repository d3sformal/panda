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

    // Method calls (boundaries) not supported (this in <init> not mapped to object(?) in test4)
    //@Test
    public static void test4() {
        D degrees = new D();

        assert -273 == degrees.celsius;
    }

    static class D {
        int celsius = -273;
    }
}
