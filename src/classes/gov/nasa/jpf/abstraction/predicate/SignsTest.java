package gov.nasa.jpf.abstraction.predicate;

public class SignsTest extends BaseTest {
    public SignsTest() {
        config.add("+apf.abstract_domain=SIGNS src/tests/" + getClass().getName().replace(".", "/") + ".signs");
    }
}
