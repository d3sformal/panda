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

public class NestedClassTest extends BaseTest {
    static class Class {
        static class NestedClass {
            boolean f;
            int g;
            int[] a;
        }
    }

    @Test
    public static void test() {
        Class.NestedClass i = new Class.NestedClass();

        if (i.f) {
            i.g = 3;
            i.a = new int[i.g + 10];
            i.a[i.g] = 4;
        }
    }
}
