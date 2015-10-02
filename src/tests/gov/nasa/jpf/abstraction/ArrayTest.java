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

class ArrA {
    int f;
}

public class ArrayTest extends BaseTest {
    static ArrA static_a[] = new ArrA[2];

    static {
        static_a[0] = new ArrA();
        static_a[1] = new ArrA();
        static_a[0].f = 1;
        static_a[1].f = 2;
        static_a[0] = static_a[1];
    }

    @Test
    public static void main(String[] args) {
        ArrA a[] = static_a;
        assertConjunction("a[0].f = 2: true");
        assertNumberOfPossibleValues("class(gov.nasa.jpf.abstraction.ArrayTest).static_a[0]", 1);
        int i = a[0].f;
        i = -1;
        a[0].f = i + 2;
    }

    @Test
    public static void test() {
        int x;
        int i = 0;
        int[] a = new int[2];

        // Load -> non-det choice of values of `i`
        // Adjusting -> set `i` to 1 in the abstract "branch" i = 1
        if (i >= 0 && i < a.length) {
            if (a[i] > 0) {
                x = a[i];
            }
        }
    }
}
