package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.Verify;

public class MultipleErrorsTest extends BaseTest {
    public MultipleErrorsTest() {
        config.add("+search.multiple_errors=true");
    }

    @FailingTest
    public static void standardAssertionErrors() {
        if (Verify.getBoolean()) {
            assert false : "Error #1";
        } else {
            assert false : "Error #2";
        }
    }

    @FailingTest
    public static void predicateAbstractionAssertionErrors() {
        if (Verify.getBoolean()) {
            assertConjunction("x = 0: true");
        } else {
            assertNumberOfPossibleValues("x", 1);
        }
    }
}
