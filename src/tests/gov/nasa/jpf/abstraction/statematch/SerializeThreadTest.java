/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
