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
        int[] a = {42};
        int i = 0;

        assert a[i] == 42;
    }
}
