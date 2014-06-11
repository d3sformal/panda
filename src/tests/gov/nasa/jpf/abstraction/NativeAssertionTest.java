package gov.nasa.jpf.abstraction;

public class NativeAssertionTest extends BaseTest {
    public NativeAssertionTest() {
        config.add("+search.multiple_errors=true");
    }

    @Test
    public static void test1() {
        int x = 0;

        assert x != 1 : "x is 1";     // Should not fail - the fact is captured by a predicate (x = 1), which valuates to `false`
    }

    @FailingTest
    public static void test2() {
        int x = 0;

        assert x == 1 : "x is not 1"; // Should fail - the fact is captured by a predicate (x = 1), which valuates to `true`
    }
}
