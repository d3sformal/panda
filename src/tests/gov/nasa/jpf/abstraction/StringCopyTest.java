package gov.nasa.jpf.abstraction;

import static gov.nasa.jpf.abstraction.Verifier.unknownInt;

// Taken from SVCOMP

public class StringCopyTest extends BaseTest {
    private static final int N = 5;

    @Test
    @Config(items = {
        "+panda.refinement=true",
        "+panda.storage.class=gov.nasa.jpf.abstraction.util.DebugCopyPreservingStateSet",
        "+vm.serializer.class=gov.nasa.jpf.abstraction.util.DebugPredicateAbstractionSerializer",
        "+listener+=,gov.nasa.jpf.listener.ExecTracker",
        "+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker",
        "+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener"
    })
    public static void test() {
        int[] src = new int[N];
        int[] dst = new int[N];

        for (int j = 0; j < N; ++j) {
            src[j] = unknownInt(); // PROBLEM: gets matched when 0 < j < N
        }

        int i = 0;

        while (src[i] != 0
            && i < src.length // Added to exclude REAL bug in the benchmark (depends on initialization of the arrays)
            ) {
            dst[i] = src[i];
            i = i + 1;
        }

        System.out.println(i);

        for (int j = 0; j < i; ++j) {
            assert dst[j] == src[j];
        }
    }
}
