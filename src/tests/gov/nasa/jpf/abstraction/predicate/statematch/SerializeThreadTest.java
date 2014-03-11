package gov.nasa.jpf.abstraction.predicate.statematch;

import gov.nasa.jpf.abstraction.predicate.BaseTest;

public class SerializeThreadTest extends BaseTest {
    public static void main(String[] args) throws Exception {
        new SerializeThread().test();
    }
}

class SerializeThread {
    public void test() throws Exception {
        final SerializeThread lockObject = this;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lockObject) {
                    // MAYBE DO SOMETHING
                }
            }
        });

        t.start();

        synchronized (lockObject) {
            // MAYBE DO SOMETHING
        }

        t.join();
    }
}
