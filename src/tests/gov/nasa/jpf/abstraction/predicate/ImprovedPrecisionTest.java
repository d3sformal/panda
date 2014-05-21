package gov.nasa.jpf.abstraction.predicate;

public class ImprovedPrecisionTest extends BaseTest {

    @Test
    public static void testCase1() {
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

    @Test
    public static void testCase2() {
        int x = 1;
        int y = 2;

        Data a = new Data();
        a.f = x;

        Data b = new Data();
        b.f = y;

        b = a;

        if (a.f > 5) {
            assertKnownValuation("b.f > 0: true");
        }
    }

    @Test
    public static void testCase3() {
        Data a = new Data();
        a.f = 10;

        Data p,r,b;

        p = a;

        r = testCasesM1(p);

        b = r;

        // makes the following predicates unknown: b.f > 0 and also r.f = 10
        b.f = identity(b.f);

        // finding affected via symbol table (aliasing) does not help because predicates about r.f are unknown too
        if (a.f > 0) {
            assertKnownValuation("b.f > 0: unknown");
        }
    }

    @Test
    public static void testCase4() {
        Data a = new Data();
        a.f = 10;

        Data p,r,b;

        p = a;

        r = testCasesM1(p);

        b = r;

        if (a.f > 5) {
            assertKnownValuation("b.f > 5: true");
        }
    }

    @Test
    public static void testCase5() {
        Data a = new Data();
        a.f = 10;

        Data p,r,b;

        p = a;

        r = testCasesM1(p);

        b = r;

        assertKnownValuation("b.f > 0: true", "b.f > 5: unknown");

        if (a.f > 5) {
            assertKnownValuation("b.f > 5: true");
        }
    }

    @Test
    public static void testCase6() {
        Data[] data = new Data[3];

        data[0] = new Data();
        data[1] = new Data();
        data[2] = new Data();

        int i = 0;

        if (data[i].f > 5) {
            if (i == 0) {
                assertKnownValuation(
                    "data[0].f > 5: true",
                    "data[1].f > 5: unknown",
                    "data[2].f > 5: unknown"
                );
            } else {
                assertKnownValuation(
                    "data[0].f > 5: unknown",
                    "data[1].f > 5: true",
                    "data[2].f > 5: unknown"
                );
            }
        }
    }

    @Test
    public static void testCase7() {
        Data a = new Data();
        a.f = 10;

        Data p,r,b;

        p = a;

        r = testCasesM1(p);

        // establishes aliasing between a and b
        assertKnownValuation("a = b: false");
        b = r;
        assertKnownValuation("a = b: true");

        // makes the following predicates unknown: b.f > 0 and also r.f = 10
        b.f = identity(b.f);

        if (a.f > 0) {
            assertKnownValuation("b.f > 0: true");
        }
    }

    private static Data testCasesM1(Data v) {
        v.g = 0;
        return v;
    }

    private static int identity(int i) {
        return i;
    }
}

class Data {
    int f;
    int g;
    int h;
}
