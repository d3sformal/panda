package gov.nasa.jpf.abstraction;

public class RestrictedPredicateScopeTest extends BaseTest {
    @Test
    public static void test() {
        int x = -1;
        int y = new C().m(x);

        assert y == 2;
    }
}

class C {
    public int m(int y) {
        int z;

        if (y < 0) {
            z = m(-y);
        } else {
            z = n(y);
        }

        // A LOT OF CODE

        return z;
    }

    private int n(int y) {
        return y * 2;
    }
}
