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

public class InterpolationTest extends BaseTest {
    public InterpolationTest() {
        config.add("+panda.refinement=true");
    }

    @Test
    public static void test1() {
        int x = 0;
        int y = 0;

        assert x == y;
    }

    @Test
    public static void test2() {
        D d = new D();

        d.val = 10;

        assert d.val == 10;
    }

    @Test
    public static void test3() {
        int[] a = new int[3];

        a[0]= 10;

        assert a[0] == 10;
    }

    static class D {
        int val;
    }
}
