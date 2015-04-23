package pc;

import static gov.nasa.jpf.abstraction.Verifier.unknownNonNegativeInt;

public class SerialProducerConsumer {
    public static final int PRODS = 2;
    public static final int CONS = 3;
    public static final int PRODS_COUNT = 3;
    public static final int CONS_COUNT = 2;
    public static int total = 0;

    public static void main(String[] args) {
        Producer[] prods = new Producer[PRODS];
        Consumer[] cons = new Consumer[CONS];
        Buffer b = new Buffer(5);

        // Allocate some producers
        for (int i = 0; i < PRODS; ++i) {
            prods[i] = new Producer(b);
        }
        // Allocate some consumers
        for (int i = 0; i < CONS; ++i) {
            cons[i] = new Consumer(b);
        }

        // Use the buffer in turns
        while (schedule(b, prods, cons)) {
        }

        // The total number of consumed items needs to equal the number of produced items
        assert total == PRODS_COUNT * PRODS;
    }

    private static boolean schedule(Buffer b, Producer[] prods, Consumer[] cons) {
        boolean prodsDone = true;
        boolean consDone = true;

        // Check whether there is any live producer
        for (int k = 0; k < prods.length; ++k) {
            if (!prods[k].isDone()) {
                prodsDone = false;
            }
        }
        // Check whether there is any live consumer
        for (int k = 0; k < cons.length; ++k) {
            if (!cons[k].isDone()) {
                consDone = false;
            }
        }

        // Deadlock
        //
        // If there are no consumers, there must be no blocked producer
        assert !consDone || !(b.isFull() && !prodsDone);
        // If there are no producers, there must be no blocked consumer
        assert !prodsDone || !(b.isEmpty() && !consDone);

        // If there are still some workers
        if (!prodsDone || !consDone) {
            int i = unknownNonNegativeInt();

            // We can pick from both types of workers
            if (!b.isEmpty() && !b.isFull() && !prodsDone && !consDone) {
                int all = prods.length + cons.length;

                i %= all;

                if (i < prods.length) {
                    wake(prods, i);
                } else {
                    wake(cons, i - prods.length);
                }
            } else if (!b.isEmpty() && !consDone) { // Only consumers can work
                i %= cons.length;

                wake(cons, i);
            } else if (!b.isFull() && !prodsDone) { // Only producers can work
                i %= prods.length;

                wake(prods, i);
            }

            return true;
        }

        return false;
    }

    private static void wake(Producer[] prods, int i) {
        int count = 0;

        while (prods[i].isDone()) {
            assert count++ < prods.length;

            i = (i + 1) % prods.length;
        }

        prods[i].step();
    }

    private static void wake(Consumer[] cons, int i) {
        int count = 0;

        while (cons[i].isDone()) {
            assert count++ < cons.length;

            i = (i + 1) % cons.length;
        }

        cons[i].step();
    }

    private static class Buffer {
        protected int size;
        protected Object[] array;
        protected int putPtr = 0;
        protected int getPtr = 0;
        protected int usedSlots = 0;

        public Buffer(int b) {
            size = b;
            array = new Object[b];
        }

        public void put(Object x) {
            assert !isFull();

            array[putPtr] = x;

            putPtr = (putPtr + 1) % size;
            ++usedSlots;
        }

        public Object get() {
            assert !isEmpty();

            Object x = array[getPtr];
            array[getPtr] = null;

            getPtr = (getPtr + 1) % size;
            --usedSlots;

            return x;
        }

        public boolean isEmpty() {
            return usedSlots == 0;
        }

        public boolean isFull() {
            return usedSlots == size;
        }
    }

    private static class Producer {
        private Buffer buffer;
        private int count = 0;
        private boolean done = false;

        private static int payload = 0;

        public Producer(Buffer b) {
            buffer = b;
        }

        public void step() {
            assert !done;

            if (count++ < PRODS_COUNT) {
                buffer.put((Integer)(++payload));
            }

            if (count >= PRODS_COUNT) {
                done = true;
            }
        }

        public boolean isDone() {
            return done;
        }

        public int getCount() {
            return count;
        }
    }

    private static class Consumer {
        private Buffer buffer;
        private int count = 0;
        private boolean done = false;

        public Consumer(Buffer b) {
            buffer = b;
        }

        public void step() {
            assert !done;

            if (count++ < CONS_COUNT) {
                Object received = buffer.get();

                if (received == null) {
                    done = true;
                }

                ++total;
            }

            if (count >= CONS_COUNT) {
                done = true;
            }
        }

        public boolean isDone() {
            return done;
        }

        public int getCount() {
            return count;
        }
    }
}
