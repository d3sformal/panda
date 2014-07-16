package gov.nasa.jpf.abstraction;

public class RangeTest extends BaseTest {
    public RangeTest(int min, int max) {
        config.add("+panda.abstract_domain=RANGE " + min + " " + max + " src/tests/" + getClass().getName().replace(".", "/") + ".range");
    }
}
