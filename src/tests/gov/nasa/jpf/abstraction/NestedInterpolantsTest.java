package gov.nasa.jpf.abstraction;

public class NestedInterpolantsTest extends BaseTest {
    public NestedInterpolantsTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.log_smt=true");
    }

    private static final int SIZE = 42;

    //@Test
    public static void test1() {
        A a = new A();

        m1();
        m2(a);
        m3();

        assert a.a.length == SIZE;
    }

    @Test
    @Config(items = {
        "+panda.refinement.nested=true",
        "+panda.refinement.custom=false",

        "+listener+=,gov.nasa.jpf.abstraction.util.ExecTracker",
        "+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker",
        "+listener+=,gov.nasa.jpf.abstraction.util.PredicateValuationMonitor",
        "+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener"
    })
    public static void test2() {
        A a = new A();

        m1();
        m2(a);
        m3();

        assert a.a.length == SIZE;
    }

    private static void m1() {
    }

    private static void m2(A a) {
    }

    private static void m3() {
    }

    private static class A {
        public int[] a = new int[SIZE];
    }
}
