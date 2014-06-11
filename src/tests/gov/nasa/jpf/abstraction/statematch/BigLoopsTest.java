package gov.nasa.jpf.abstraction.statematch;

public class BigLoopsTest extends StateMatchingTest {
    public static void main(String[] args) {
        int[] data = new int[10];
        int i = zero();
        int pos = 0;

        assertVisitedAtMost(1);

        while (i < 100) {
            assertVisitedAtMostWithValuation(0, "pos < 0: true");
            assertVisitedAtMostWithValuation(1, "pos = 0: true");
            assertVisitedAtMostWithValuation(1, "pos = 1: true");
            assertVisitedAtMostWithValuation(1, "pos = 2: true");
            assertVisitedAtMostWithValuation(1, "pos = 3: true");
            assertVisitedAtMostWithValuation(1, "pos = 4: true");
            assertVisitedAtMostWithValuation(1, "pos = 5: true");
            assertVisitedAtMostWithValuation(1, "pos = 6: true");
            assertVisitedAtMostWithValuation(1, "pos = 7: true");
            assertVisitedAtMostWithValuation(1, "pos = 8: true");
            assertVisitedAtMostWithValuation(1, "pos = 9: true");
            assertVisitedAtMostWithValuation(0, "pos > 9: true");

            assertVisitedAtMost(10);
            assertRevisitedAtLeast(9);

            data[pos] = i;
            pos++;

            if (pos >= data.length) pos = 0; // State matching

            i++;
        }

        // The previous loop condition is examined 10 times
        // There is 10 possibilities when to break the looping
        assertVisitedAtMost(10);
        assertRevisitedAtLeastWithValuation(9, "i >= 100: true");

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
