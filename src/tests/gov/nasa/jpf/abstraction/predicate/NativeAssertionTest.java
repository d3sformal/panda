package gov.nasa.jpf.abstraction.predicate;

public class NativeAssertionTest extends BaseTest {
    public NativeAssertionTest() {
        config.add("+search.multiple_errors=true");
    }

    @FailingTest
    public static void main(String[] args) {
        int x = 0;

        assert x != 1 : "x is 1";     // Should not fail - the fact is captured by a predicate (x = 1), which valuates to `false`

        assert x == 0 : "x is not 0"; // Should fail - there is no predicate capturing the asserted fact.
        assert x == 1 : "x is not 1"; // Should fail - the fact is captured by a predicate (x = 1), which valuates to `true`
    }
}
