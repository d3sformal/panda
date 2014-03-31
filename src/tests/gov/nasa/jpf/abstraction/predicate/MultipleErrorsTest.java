package gov.nasa.jpf.abstraction.predicate;

public class MultipleErrorsTest extends BaseTest {
    public MultipleErrorsTest() {
        config.add("+search.multiple_errors=true");
    }

    public static boolean unknown = true;

    @FailingTest
    public static void standardAssertionErrors() {
        if (unknown) {
            assert false : "Error #1";
        } else {
            assert false : "Error #2";
        }
    }

    @FailingTest
    public static void predicateAbstractionAssertionErrors() {
        if (unknown) {
            assertConjunction("x = 0: true");
        } else {
            assertNumberOfPossibleValues("x", 1);
        }
    }
}
