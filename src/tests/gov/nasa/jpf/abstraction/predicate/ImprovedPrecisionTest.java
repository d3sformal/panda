package gov.nasa.jpf.abstraction.predicate;

class Data {
    int f;
    int g;
    int h;
}

public class ImprovedPrecisionTest extends BaseTest {
    public static void main(String[] args) {
        int x = 5;

        Data a = new Data();
        a.f = -1;

        Data b = new Data();
        b.f = x;
        b.g = 8;
        b.h = 10;

        a = b;

        assertKnownValuation("a = b: true", "b.f > 0: unknown");

        if (a.f > 0) {
            assertKnownValuation("b.f > 0: true");
        }
    }
}
