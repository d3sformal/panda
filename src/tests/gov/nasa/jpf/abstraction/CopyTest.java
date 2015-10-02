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

public class CopyTest extends BaseTest {
    public CopyTest() {
        config.add("+panda.refinement=true");
    }

    private static final int N = 5;

    private static void copy2() {
        int[] a1 = new int[N];
        int[] a2 = new int[N];
        int[] a3 = new int[N];

        for (int i = 0; i < a1.length; ++i) {
            int n = a1[i];
            a2[i] = n;
        }
        for (int i = 0; i < a2.length; ++i) {
            int n = a2[i];
            a3[i] = n;
        }

        for (int i = 0; i < a1.length; ++i) {
            assert a1[i] == a3[i];
        }
    }

    @Test
    public static void test1() {
        copy2();
    }

    @Test
    @Config(items = {
        "+panda.branch.adjust_concrete_values=false",
        "+panda.branch.prune_infeasible=true"
    })
    public static void test2() {
        copy2();
    }
}
