package gov.nasa.jpf.abstraction;

public class IntervalTest extends BaseTest {
    public IntervalTest(int min, int max) {
        config.add("+panda.abstract_domain=INTERVAL " + min + " " + max + " src/tests/" + getClass().getName().replace(".", "/") + ".interval");
    }
}
