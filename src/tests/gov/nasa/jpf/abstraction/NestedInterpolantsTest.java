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

public class NestedInterpolantsTest extends BaseTest {
    public NestedInterpolantsTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.log_smt=true");
    }

    private static final int SIZE = 42;

    //@Test
    public static void test1() {
        A a = new A();

        m1();
        m2(a);
        m3();

        assert a.a.length == SIZE;
    }

    @Test
    @Config(items = {
        "+panda.refinement.nested=true",
        "+panda.refinement.custom=false",

        "+listener+=,gov.nasa.jpf.abstraction.util.ExecTracker",
        "+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker",
        "+listener+=,gov.nasa.jpf.abstraction.util.PredicateValuationMonitor",
        "+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener"
    })
    public static void test2() {
        A a = new A();

        m1();
        m2(a);
        m3();

        assert a.a.length == SIZE;
    }

    private static void m1() {
    }

    private static void m2(A a) {
    }

    private static void m3() {
    }

    private static class A {
        public int[] a = new int[SIZE];
    }
}
