package gov.nasa.jpf.abstraction;

public class LongSignsTest extends SignsTest {
    @Test
    public static void test1() {
        float x = test_L2F(1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test2() {
        double x = test_L2D(1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test3() {
        int x = test_L2I(1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test4() {
        long x = test_LADD(3L, -2L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test5() {
        long x = test_LADD(3L, 1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test6() {
        long x = test_LADD(-1L, 0L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test7() {
        long x = test_LAND(0L, 0L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //@Test
    public static void test8() {
        long x = test_LAND(0L, 1L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //@Test
    public static void test9() {
        long x = test_LAND(1L, 0L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //@Test
    public static void test10() {
        long x = test_LAND(1L, 1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test11() {
        boolean x = test_IF_LCMPEQ(3L, -1L);

        assertConjunction("x > 0: false");
    }

    @Test
    public static void test12() {
        boolean x = test_IF_LCMPNE(3L, -1L);

        assertConjunction("x > 0: true");
    }

    @Test
    public static void test13() {
        boolean x = test_IF_LCMPLT(3L, -1L);

        assertConjunction("x > 0: false");
    }

    @Test
    public static void test14() {
        boolean x = test_IF_LCMPLE(1L, 0L);

        assertConjunction("x > 0: false");
    }

    @Test
    public static void test15() {
        boolean x = test_IF_LCMPGT(1L, 0L);

        assertConjunction("x > 0: true");
    }

    @Test
    public static void test16() {
        boolean x = test_IF_LCMPGE(1L, 0L);

        assertConjunction("x > 0: true");
    }

    //@Test
    public static void test17() {
        long x = test_LMUL(3L, -1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test18() {
        long x = test_LMUL(-3L, 1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test19() {
        long x = test_LMUL(-3L, -2L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test20() {
        long x = test_LMUL(2L, -1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test21() {
        long x = test_LNEG(-3L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test22() {
        long x = test_LNEG(0L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test23() {
        long x = test_LNEG(3L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test24() {
        long x = test_LOR(0L, 0L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test25() {
        long x = test_LOR(0L, 1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test26() {
        long x = test_LOR(1L, 0L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test27() {
        long x = test_LOR(1L, 1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test28() {
        long x = test_LSHL1(1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test29() {
        long x = test_LSHL10(0L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test30() {
        long x = test_LSHLm3(1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test31() {
        long x = test_LSHLm3(-1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //1 / ALOT > 0
    //@Test
    public static void test32() {
        long x = test_LSHR1(1L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test33() {
        long x = test_LSHR10(0L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //1 / ALOT > 0
    //@Test
    public static void test34() {
        long x = test_LSHRm3(1L);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test35() {
        long x = test_LSHRm3(-1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test36() {
        long x = test_LSUB(3L, -1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test37() {
        long x = test_LSUB(3L, 1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test38() {
        long x = test_LSUB(3L, 7L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test39() {
        long x = test_LSUB(-1L, 0L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test40() {
        long x = test_LUSHR1(-1L);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    //@Test
    public static void test41() {
        long x = test_LXOR(0L, 0L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test42() {
        long x = test_LXOR(0L, 1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test43() {
        long x = test_LXOR(0L, 1L);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test44() {
        long x = test_LXOR(1L, 1L);

        assertConjunction("x > 0: false", "x < 0: true");
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
