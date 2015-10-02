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

public class LongRangeTest extends RangeTest {
    public LongRangeTest() {
        super(-10, 10);
    }

    @Test
    public static void test1() {
        float x = test_L2F(1);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test2() {
        double x = test_L2D(1);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test3() {
        int x = test_L2I(1);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test4() {
        long x = test_LADD(3, -2);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test5() {
        long x = test_LADD(3, 1);

        assertConjunction("x = 4: true");
    }

    @Test
    public static void test6() {
        long x = test_LADD(-1, 0);

        assertConjunction("x = -1: true");
    }

    //@Test
    public static void test7() {
        long x = test_LAND(0, 0);

        assertConjunction("x = 0: true");
    }

    //@Test
    public static void test8() {
        long x = test_LAND(0, 1);

        assertConjunction("x = 0: true");
    }

    //@Test
    public static void test9() {
        long x = test_LAND(1, 0);

        assertConjunction("x = 0: true");
    }

    //@Test
    public static void test10() {
        long x = test_LAND(1, 1);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test11() {
        boolean x = test_IF_LCMPEQ(3, -1);

        assertConjunction("x = 0: true");
    }

    @Test
    public static void test12() {
        boolean x = test_IF_LCMPNE(3, -1);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test13() {
        boolean x = test_IF_LCMPLT(3, -1);

        assertConjunction("x = 0: true");
    }

    @Test
    public static void test14() {
        boolean x = test_IF_LCMPLE(1, 0);

        assertConjunction("x = 0: true");
    }

    @Test
    public static void test15() {
        boolean x = test_IF_LCMPGT(1, 0);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test16() {
        boolean x = test_IF_LCMPGE(1, 0);

        assertConjunction("x = 1: true");
    }

    //@Test
    public static void test21() {
        long x = test_LMUL(3, -1);

        assertConjunction("x = -3: true");
    }

    //@Test
    public static void test22() {
        long x = test_LMUL(-3, 1);

        assertConjunction("x = -3: true");
    }

    //@Test
    public static void test23() {
        long x = test_LMUL(-3, -2);

        assertConjunction("x = 6: true");
    }

    //@Test
    public static void test24() {
        long x = test_LMUL(2, -1);

        assertConjunction("x = -2: true");
    }

    @Test
    public static void test25() {
        long x = test_LNEG(-3);

        assertConjunction("x = 3: true");
    }

    @Test
    public static void test26() {
        long x = test_LNEG(0);

        assertConjunction("x = 0: true");
    }

    @Test
    public static void test27() {
        long x = test_LNEG(3);

        assertConjunction("x = -3: true");
    }

    //@Test
    public static void test28() {
        long x = test_LOR(0, 0);

        assertConjunction("x = 0: true");
    }

    //@Test
    public static void test29() {
        long x = test_LOR(0, 1);

        assertConjunction("x = 1: true");
    }

    //@Test
    public static void test30() {
        long x = test_LOR(1, 0);

        assertConjunction("x = 1: true");
    }

    //@Test
    public static void test31() {
        long x = test_LOR(1, 1);

        assertConjunction("x = 1: true");
    }

    @Test
    public static void test32() {
        long x = test_LSHL1(1);

        assertConjunction("x = 2: true");
    }

    @Test
    public static void test33() {
        long x = test_LSHL10(0);

        assertConjunction("x = 0: true");
    }

    @Test
    public static void test34() {
        long x = test_LSHLm3(1);

        assertConjunction("x > 10: true");
    }

    @Test
    public static void test35() {
        long x = test_LSHLm3(-1);

        assertConjunction("x < -10: true");
    }

    //1 / ALOT > 0
    //@Test
    public static void test36() {
        long x = test_LSHR1(1);

        assertConjunction();
    }

    @Test
    public static void test37() {
        long x = test_LSHR10(0);

        assertConjunction("x = 0: true");
    }

    //1 / ALOT > 0
    //@Test
    public static void test38() {
        long x = test_LSHRm3(1);

        assertConjunction();
    }

    @Test
    public static void test39() {
        long x = test_LSHRm3(-1);

        assertConjunction();
    }

    @Test
    public static void test40() {
        long x = test_LSUB(3, -1);

        assertConjunction("x = 4: true");
    }

    @Test
    public static void test41() {
        long x = test_LSUB(3, 1);

        assertConjunction("x = 2: true");
    }

    @Test
    public static void test42() {
        long x = test_LSUB(3, 7);

        assertConjunction("x = -4: true");
    }

    @Test
    public static void test43() {
        long x = test_LSUB(-1, 0);

        assertConjunction("x = -1: true");
    }

    //@Test
    public static void test44() {
        long x = test_LUSHR1(-1);

        assertConjunction();
    }

    //@Test
    public static void test45() {
        long x = test_LXOR(0, 0);

        assertConjunction("x = 0: true");
    }

    //@Test
    public static void test46() {
        long x = test_LXOR(0, 1);

        assertConjunction("x = 1: true");
    }

    //@Test
    public static void test47() {
        long x = test_LXOR(0, 1);

        assertConjunction("x = 1: true");
    }

    //@Test
    public static void test48() {
        long x = test_LXOR(1, 1);

        assertConjunction("x = 0: true");
    }

    public static float test_L2F(long x) {
        return (float)x;
    }

    public static double test_L2D(long x) {
        return (double)x;
    }

    public static int test_L2I(long x) {
        return (int)x;
    }

    public static long test_LADD(long x, long y) {
        return x + y;
    }

    public static long test_LAND(long x, long y) {
        return x & y;
    }

    public static boolean test_IF_LCMPEQ(long x, long y) {
        if (x != y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_LCMPNE(long x, long y) {
        if (x == y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_LCMPLT(long x, long y) {
        if (x >= y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_LCMPLE(long x, long y) {
        if (x > y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_LCMPGT(long x, long y) {
        if (x <= y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_LCMPGE(long x, long y) {
        if (x < y) {
            return false;
        }

        return true;
    }

    public static long test_LMUL(long x, long y) {
        return x * y;
    }

    public static long test_LNEG(long x) {
        return -x;
    }

    public static long test_LOR(long x, long y) {
        return x | y;
    }

    public static long test_LSHL1(long x) {
        return x << 1;
    }

    public static long test_LSHL10(long x) {
        return x << 10;
    }

    public static long test_LSHLm3(long x) {
        return x << -3;
    }

    public static long test_LSHR1(long x) {
        return x >> 1;
    }

    public static long test_LSHR10(long x) {
        return x >> 10;
    }

    public static long test_LSHRm3(long x) {
        return x >> -3;
    }

    public static long test_LSUB(long x, long y) {
        return x - y;
    }

    public static long test_LUSHR1(long x) {
        return x >>> 1;
    }

    public static long test_LXOR(long x, long y) {
        return x ^ y;
    }
}
