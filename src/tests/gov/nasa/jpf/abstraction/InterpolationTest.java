package gov.nasa.jpf.abstraction;

class D{public static void main(String[] args) {new InterpolationTest().bootstrap();}}
public class InterpolationTest extends BaseTest {
    public InterpolationTest() {
        config.add("+panda.interpolation=true");
    }

    @FailingTest
    public static void test1() {
        int x = 0;
        int y = 0;

        assertConjunction("x = y + 1: true");
    }

    @FailingTest
    public static void test2() {
        D d = new D();

        d.val = 10;

        assertConjunction("d.val = 0: true");
    }

    static class D {
        int val;
    }
}
