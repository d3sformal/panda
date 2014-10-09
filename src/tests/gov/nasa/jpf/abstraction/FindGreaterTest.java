package gov.nasa.jpf.abstraction;

public class FindGreaterTest extends BaseTest {
    public FindGreaterTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.refinement.global=false");
        config.add("+panda.abstract_domain=PREDICATES");
        config.add("+panda.log_smt=true");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.PredicateValuationMonitor");
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.Stepper");
        config.add("+report.console.property_violation=error,trace,snapshot");
        config.add("+report.console.show_code=true");
    }

    @Test
    public static void test1() {
        int[] data = new int[5];

        loadRandomValues(data);

        int pos = findGreater(data, 10);

        assert pos == data.length || data[pos] > 10;
    }

    @Test
    public static void test2() {
        int[] data = new int[5];

        int pos = data.length;

        for (int i = 0; i < data.length; ++i) {
            if (data[i] > 10) {
                pos = i;
                break;
            }
        }

        assert pos == data.length || data[pos] > 10;

    }

    private static void loadRandomValues(int[] a) {
        /* havoc(a) */
    }

    private static int findGreater(int[] a, int t) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i] > t) {
                return i;
            }
        }

        return a.length;
    }
}
