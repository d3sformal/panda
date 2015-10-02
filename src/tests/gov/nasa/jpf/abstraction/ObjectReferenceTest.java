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

// Treat references using abstract heap representation
// No need for predicates (refinement)
public class ObjectReferenceTest extends BaseTest {
    @Test
    public static void test1() {
        Object o1 = new Object();
        Object o2 = new Object();

        assert o1 != o2;
    }

    @Test
    public static void test2() {
        Object o1 = new Object();
        Object o2 = o1;

        assert o1 == o2;
    }

    @Test
    @Config(items = {
        "+vm.gc=false"
    })
    public static void test3() {
        Object[] a = new Object[2];
        a[0] = new Object();
        a[1] = new Object();

        int unknownIndex = 0;
        a[unknownIndex] = new Object();

        Object[] b = a;

        assert a[0] == b[0];
    }
}
