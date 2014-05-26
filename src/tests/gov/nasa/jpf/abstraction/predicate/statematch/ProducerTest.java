package gov.nasa.jpf.abstraction.predicate.statematch;

// small test for matching predicates together with abstract heap
// both semantically equivalent and semantically different objects

public class ProducerTest extends StateMatchingTest {
    public ProducerTest() {
        disableStateMatching();
        enableBranchPruning();
    }

    public static void main(String[] args) {
        Producer p = new Producer();

        p.doIt();
    }
}

class Producer {
    public static final int SIZE = 8;

    private static class Data {
        public int val;
    }

    private Data[] buffer = new Data[SIZE];

    public void doIt() {
        int pos = 0;

        for (int i = 0; i < 4; ++i) {
            // simulate modulo by 3
            int v = pos + pos + 1;
            while (v >= 3) v = v - 3;

            Data d = new Data();
            d.val = v;

            int oldPos = pos;
            while (buffer[pos] != null) {
                // simulate modulo
                pos = pos + pos + pos + 1;
                while (pos >= SIZE) pos = pos - SIZE;

                if (pos == oldPos) break;
            }

            System.out.println("P: d.val = " + d.val);

            buffer[pos] = d;
        }
    }
}
