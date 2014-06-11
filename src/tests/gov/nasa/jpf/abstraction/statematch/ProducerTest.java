package gov.nasa.jpf.abstraction.statematch;

// small test for matching predicates together with abstract heap
// both semantically equivalent and semantically different objects

import static gov.nasa.jpf.abstraction.BaseTest.*;

public class ProducerTest extends StateMatchingTest {
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

            assertConjunction("v < 3: true");

            Data d = new Data();
            d.val = v;

            // simulate modulo
            pos = pos + 1;
            if (pos >= SIZE) pos = 0;

            assertConjunction("pos < 8: true");

            System.out.println("P: d.val = " + d.val);

            buffer[pos] = d;
        }

        assertConjunction("i >= 4: true");
    }
}
