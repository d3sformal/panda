package gov.nasa.jpf.abstraction;

public class SignsTest extends BaseTest {
    public SignsTest() {
        config.add("+panda.abstract_domain=SIGNS src/tests/" + getClass().getName().replace(".", "/") + ".signs");
    }
}
