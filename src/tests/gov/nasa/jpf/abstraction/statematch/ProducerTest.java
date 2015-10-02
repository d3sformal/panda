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

// small test for matching predicates together with abstract heap
// both semantically equivalent and semantically different objects

import static gov.nasa.jpf.abstraction.BaseTest.*;
import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.*;

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
            assertVisitedAtMost(4);
            assertRevisitedAtLeast(3);

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

            assertConjunction("this.buffer[pos].val < 3: true");
        }

        assertConjunction("i >= 4: true");
    }
}
