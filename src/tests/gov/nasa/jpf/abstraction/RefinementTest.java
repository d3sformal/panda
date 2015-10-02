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

import static gov.nasa.jpf.abstraction.statematch.StateMatchingTest.*;

public class RefinementTest extends BaseTest {
    public RefinementTest() {
        config.add("+panda.refinement=true");
        config.add("+panda.refinement.global=true");
    }

    @Test
    public static void test1() {
        int i = 0;

        assert i == 0;
    }

    @Test
    public static void test2() {
        int[] a = new int[1];

        a[0] = 42;

        int i = 0;

        assert a[i] == 42;
    }

    @Test
    public static void test3() {
        D degrees = new D();

        degrees.celsius = -273;

        assert -273 == degrees.celsius;
    }

    // Interpolants over anonymous objects (this = fresh) not yet supported properly
    @Test
    public static void test4() {
        // new
        // invoke <init>
        //   - this is another method
        //   - a parameter binding clauses is added to the trace (this = fresh_xyz)
        //
        // when interpolating after astore in the method test4, the symbol fresh_xyz is live (because it appears in the previous steps and in <init> param-binding which is added to the end of the interpolation query)
        // therefore it is possible that the interpolant will look like fresh_xyz.celsius = -273 instead of degrees.celsius = -273
        // but if we also use `panda.refinement.global` then this does not happen
        D degrees = new D();

        assert -273 == degrees.celsius;
    }

    @Test
    public static void test5() {
        int x = 2;

        assert f(x) == 3;
    }

    @Test
    public static void test6() {
        int x = 0;

        assert g(x) == 3;
    }

    @Test
    public static void test7() {
        assertVisitedAtMost(2); // Ensure second refinement does not backtrack completely

        // Ensure refinement in different methods
        h();
    }

    @Test
    public static void test8() {
        int[] arr = new int[3];

        arr[0] = 0;
        arr[1] = 1;
        arr[2] = 2;

        int i = 0;

        if (i >= 0 && i < arr.length) {
            // choice over arr[?]
            int d = arr[i];

            int e = 0;
            assert e == 0; // unrelated spurious error
        }
    }

    private static int f(int x) {
        return x + 1;
    }

    private static int g(int x) {
        return 3;
    }

    private static void h() {
        int x = 0;

        assert x == 0; // creates new states (1 ok, 1 spurious error)

        // Get here after first refinement

        i();
    }

    private static void i() {
        int x = 0;

        assert x == 0; // creates new states (1 ok, 1 spurious error)

        // Get here after second refinement
    }

    static class D {
        int celsius = -273;
    }
}
