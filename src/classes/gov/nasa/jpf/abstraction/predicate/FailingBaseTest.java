package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.JPF;
import static org.junit.Assert.assertTrue;

public class FailingBaseTest extends BaseTest {
    @Override
    protected boolean checkPassed(JPF jpf) {
        return jpf.foundErrors();
    }
}
