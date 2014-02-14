package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.JPF;
import static org.junit.Assert.assertTrue;

public class NegativeBaseTest extends BaseTest {
    @Override
    protected void checkResult(JPF jpf) {
        assertTrue(jpf.foundErrors());
    }
}
