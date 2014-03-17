package gov.nasa.jpf.abstraction.predicate;

public class AssumeTest extends BaseTest {
    public static void main(String[] args) {
        int c;

        c = m1();

        assertKnownValuation("c = 3: true");

        m2(c);
    }

    public static int m1() {
        return 6;
    }

    public static void m2(int c) {
        assertKnownValuation("c = 7: true");

        c = 3;

        assertKnownValuation("c = 7: false");
    }
}
