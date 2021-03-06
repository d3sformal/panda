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
package gov.nasa.jpf.abstraction;

public class SchedulerTest extends BaseTest {
    public static void main(String[] args) {
        ThreadInfo[] id2thread = new ThreadInfo[3];

        assertConjunction("alength(arrlen, id2thread) = 3: true");

        // prepare a few threads
        id2thread[0] = new ThreadInfo(5);
        id2thread[1] = new ThreadInfo(18);
        id2thread[2] = new ThreadInfo(10);

        assertNumberOfPossibleValues("id2thread[0]", 1);
        assertNumberOfPossibleValues("id2thread[1]", 1);
        assertNumberOfPossibleValues("id2thread[2]", 1);

        assertNotAliased("id2thread[0]", "id2thread[1]", "id2thread[2]");

        // some threads are put into the active state
        id2thread[1].active = true;
        id2thread[2].active = true;

        // compute order in which threads should run
        int schedule_size = 0;
        int[] schedule = new int[SchedulerConfig.SIZE];

        assertConjunction("alength(arrlen, schedule) = 5: true");

        // set of active threads is iterated when to make scheduling decisions
        for (int k = 0; k < id2thread.length; ++k) {
            assertDisjunction("k = 0: true", "k = 1: true", "k = 2: true");
            assertDisjunction("k = 0: false", "k = 1: false");
            assertDisjunction("k = 1: false", "k = 2: false");
            assertDisjunction("k = 0: false", "k = 2: false");

            ThreadInfo actTh = id2thread[k];

            assertExclusiveDisjunction("actTh = id2thread[0]: true", "actTh = id2thread[1]: true", "actTh = id2thread[2]: true");

            if (!actTh.active) continue;

            if (schedule_size == 0)
            {
                schedule[0] = k;
                ++schedule_size;
            }
            else
            {
                // the info object is retrieved for each active thread
                for (int i = 0; i < schedule_size; ++i) {
                    ThreadInfo schTh = id2thread[schedule[i]];

                    if (actTh.priority > schTh.priority) {
                        // insert into the scheduling queue
                        for (int j = schedule_size - 1; j >= i; --j) {
                            schedule[j + 1] = schedule[j];
                        }
                           schedule[i] = k;
                        ++schedule_size;
                        break;
                    }
                }
            }
        }
    }
}

class SchedulerConfig {
    public static int SIZE = 5;
}

class ThreadInfo {
    public int priority;
    public boolean active;

    public ThreadInfo(int prio_) {
        active = false;
        priority = prio_;
    }
}
