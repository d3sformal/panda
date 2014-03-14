package gov.nasa.jpf.abstraction.predicate;

public class ImprovedPrecisionTest extends BaseTest {
    public static void main(String[] args) {
		testCase1();
		testCase2();
		testCase3();
	}

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

	public static void testCase3()
	{
		Data a = new Data();
		a.f = 10;

		Data p,r,b;

		p = a;

		r = testCase3M1(p);

		b = r;

		if (a.f > 0) {
			assertKnownValuation("b.f > 0: true");
		}
	}

	private static Data testCase3M1(Data v) {
		v.g = 0;
		return v;
	}
}


class Data {
    int f;
    int g;
    int h;
}

