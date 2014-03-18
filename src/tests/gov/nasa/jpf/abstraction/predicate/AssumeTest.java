package gov.nasa.jpf.abstraction.predicate;

public class AssumeTest extends BaseTest {
    public static void main(String[] args) {
        int c;

        c = m1();

        assertKnownValuation("c = 0: true");

        c = m2();

        assertKnownValuation("c = 1: true");

        m3(c);
    }

    public static int m1() {
        return 0;
    }

    public static int m2() {
        return 1;
    }

    public static void m3(int c) {
        assertKnownValuation("c = 2: true");

        c = 3;

        assertKnownValuation("c = 2: false");
    }
}
