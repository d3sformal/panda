package gov.nasa.jpf.abstraction.predicate.statematch;

public class BigLoopsTest extends StateMatchingTest {
    public static void main(String[] args) {
        int[] data = new int[10];
        int i = zero();
        int pos = 0;

        assertVisitedAtMost(1);

        while (i < 100) {
            assertValuationVisitedAtMost(0, "pos < 0: true");
            assertValuationVisitedAtMost(2, "pos = 0: true"); // The state is the same after executing for the second time (but no state matching yet)
            assertValuationVisitedAtMost(1, "pos = 1: true");
            assertValuationVisitedAtMost(1, "pos = 2: true");
            assertValuationVisitedAtMost(1, "pos = 3: true");
            assertValuationVisitedAtMost(1, "pos = 4: true");
            assertValuationVisitedAtMost(1, "pos = 5: true");
            assertValuationVisitedAtMost(1, "pos = 6: true");
            assertValuationVisitedAtMost(1, "pos = 7: true");
            assertValuationVisitedAtMost(1, "pos = 8: true");
            assertValuationVisitedAtMost(1, "pos = 9: true");
            assertValuationVisitedAtMost(0, "pos > 9: true");

            assertVisitedAtMost(11);
            assertRevisitedAtLeast(10);

            data[pos] = i;
            pos++;

            if (pos >= data.length) pos = 0; // State matching

            i++;
        }

        //assertVisitedAtMost(10);
        assertValuationRevisitedAtLeast(10, "i >= 100: true");

        int total = 0;

        for (int j = 0; j < data.length; ++j) {
            int v = data[j];

            total += v;
        }

        System.out.println("total = " + total);
    }

    private static int zero() {
        return 0;
    }
}
