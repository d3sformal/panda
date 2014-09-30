package gov.nasa.jpf.abstraction;

public class FineGrainedAbstractionTest extends BaseTest {
    public FineGrainedAbstractionTest() {
        config.add("+panda.log_smt=true");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.PredicateValuationMonitor");
    }

    @Test
    public static void test() {
        int x = 0;

        assert x >= 0;
    }
}
