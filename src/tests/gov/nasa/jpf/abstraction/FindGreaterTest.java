package gov.nasa.jpf.abstraction;

class FGD {public static void main(String[] args) {new FindGreaterTest().bootstrap();}}
public class FindGreaterTest extends BaseTest {
    public FindGreaterTest() {
        config.add("+panda.interpolation=true");
        config.add("+panda.abstract_domain=PREDICATES");
        config.add("+panda.log_smt=true");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.PredicateValuationMonitor");
        config.add("+listener+=,gov.nasa.jpf.abstraction.util.Stepper");
    }

    @Test
    public static void test() {
        int[] data = new int[5];

        loadRandomValues(data);

        int pos = findGreater(data, 10);

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
