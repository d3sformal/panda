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

public class ProdConsTest extends BaseTest {
    public static final int DAILY_LIMIT = 1000000;

    public static void main(String[] args) {
        Data d = new Data();
        new Producer(d).start();
        new Consumer(d).start();
    }

    static class Data {
        public int value = 0;
        public boolean isNew = false;
    }

    static class Producer extends Thread {
        private Data d;

        public Producer(Data d) {
            super("Producer");
            this.d = d;
        }

        public void run() {
            while (true) {
                // one iteration corresponds to one production day
                int remaining = DAILY_LIMIT;
                while (remaining > 0) {
                    synchronized (d) {
                        d.value = 10; // dummy value
                        d.isNew = true;
                    }
                    --remaining;
                }
            }
        }
    }

    static class Consumer extends Thread {
        private Data d;

        public Consumer(Data d) {
            super("Consumer");
            this.d = d;
        }

        public void run() {
            while (true) {
                synchronized (d) {
                    if (d.isNew) {
                        d.isNew = false;
                        System.out.println(d.value);
                    }
                }
            }
        }
    }
}
