package gov.nasa.jpf.abstraction.statematch;

import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.*;

class Driver {
    public static void main(String[] args) {
        new SerializeThreadTest().bootstrap();
    }
}

public class SerializeThreadTest extends StateMatchingTest {
    public SerializeThreadTest() {
        config.add("+vm.por=false");
    }

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
                    // << POINT B >>
                    assertVisitedAtMost(4);
                    assertRevisitedAtLeast(3);
                }
            }
        });

        t.start();

        synchronized (lockObject) {
            // << POINT A >>
            assertVisitedAtMost(4);
            assertRevisitedAtLeast(3);
        }

        t.join();
    }
}

/*
 * PASSES:
 *
 * t1: start
 * t1: monitor
 * t1: <<POINT A>>
 * t1: join
 * t2: run
 * t2: monitor
 * t2: <<POINT B>>
 * t2: terminate
 * t1: terminate
 *
 *
 * t1: start
 * t2: run
 * t2: monitor
 * t1: monitor
 * t1: <<POINT A>>
 * t1: join
 * t2: <<POINT B>>
 * t2: terminate
 * t1: terminate
 *
 *
 * t1: start
 * t2: run
 * t2: monitor
 * t1: monitor
 * t2: <<POINT B>>
 * t2: terminate
 * t1: <<POINT A>>
 * t1: join
 * t1: terminate
 *
 *
 * t1: start
 * t2: run
 * t2: monitor
 * t2: <<POINT B>>
 * t2: terminate
 * t1: monitor
 * t1: <<POINT A>>
 * t1: join
 * t1: terminate
 */
