package gov.nasa.jpf.abstraction;

public class IntSignsTest extends SignsTest {
    @Test
    public static void test1() {
        float x = test_I2F(1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test2() {
        double x = test_I2D(1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test3() {
        long x = test_I2L(1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test4() {
        int x = test_IADD(3, -2);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test5() {
        int x = test_IADD(3, 1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test6() {
        int x = test_IADD(-1, 0);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test7() {
        int x = test_IAND(0, 0);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //@Test
    public static void test8() {
        int x = test_IAND(0, 1);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //@Test
    public static void test9() {
        int x = test_IAND(1, 0);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //@Test
    public static void test10() {
        int x = test_IAND(1, 1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test11() {
        boolean x = test_IF_ICMPEQ(3, -1);

        assertConjunction("x > 0: false");
    }

    @Test
    public static void test12() {
        boolean x = test_IF_ICMPNE(3, -1);

        assertConjunction("x > 0: true");
    }

    @Test
    public static void test13() {
        boolean x = test_IF_ICMPLT(3, -1);

        assertConjunction("x > 0: false");
    }

    @Test
    public static void test14() {
        boolean x = test_IF_ICMPLE(1, 0);

        assertConjunction("x > 0: false");
    }

    @Test
    public static void test15() {
        boolean x = test_IF_ICMPGT(1, 0);

        assertConjunction("x > 0: true");
    }

    @Test
    public static void test16() {
        boolean x = test_IF_ICMPGE(1, 0);

        assertConjunction("x > 0: true");
    }

    @Test
    public static void test17() {
        int x = test_IINC(-2, +1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test18() {
        int x = test_IINC(0, +1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test19() {
        int x = test_IINC(0, -1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test20() {
        int x = test_IINC(2, -1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    //@Test
    public static void test21() {
        int x = test_IMUL(3, -1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test22() {
        int x = test_IMUL(-3, 1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test23() {
        int x = test_IMUL(-3, -2);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test24() {
        int x = test_IMUL(2, -1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test25() {
        int x = test_INEG(-3);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test26() {
        int x = test_INEG(0);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test27() {
        int x = test_INEG(3);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test28() {
        int x = test_IOR(0, 0);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test29() {
        int x = test_IOR(0, 1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test30() {
        int x = test_IOR(1, 0);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test31() {
        int x = test_IOR(1, 1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test32() {
        int x = test_ISHL1(1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test33() {
        int x = test_ISHL10(0);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test34() {
        int x = test_ISHLm3(1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test35() {
        int x = test_ISHLm3(-1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //1 / ALOT > 0
    //@Test
    public static void test36() {
        int x = test_ISHR1(1);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test37() {
        int x = test_ISHR10(0);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    //1 / ALOT > 0
    //@Test
    public static void test38() {
        int x = test_ISHRm3(1);

        assertConjunction("x > 0: false", "x < 0: false");
    }

    @Test
    public static void test39() {
        int x = test_ISHRm3(-1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test40() {
        int x = test_ISUB(3, -1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test41() {
        int x = test_ISUB(3, 1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    @Test
    public static void test42() {
        int x = test_ISUB(3, 7);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    @Test
    public static void test43() {
        int x = test_ISUB(-1, 0);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test44() {
        int x = test_IUSHR1(-1);

        assertConjunction("x > 0: true", "x < 0: false");
    }

    //@Test
    public static void test45() {
        int x = test_IXOR(0, 0);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test46() {
        int x = test_IXOR(0, 1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test47() {
        int x = test_IXOR(0, 1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    //@Test
    public static void test48() {
        int x = test_IXOR(1, 1);

        assertConjunction("x > 0: false", "x < 0: true");
    }

    public static float test_I2F(int x) {
        return (float)x;
    }

    public static double test_I2D(int x) {
        return (double)x;
    }

    public static long test_I2L(int x) {
        return (long)x;
    }

    public static int test_IADD(int x, int y) {
        return x + y;
    }

    public static int test_IAND(int x, int y) {
        return x & y;
    }

    public static boolean test_IF_ICMPEQ(int x, int y) {
        if (x != y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_ICMPNE(int x, int y) {
        if (x == y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_ICMPLT(int x, int y) {
        if (x >= y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_ICMPLE(int x, int y) {
        if (x > y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_ICMPGT(int x, int y) {
        if (x <= y) {
            return false;
        }

        return true;
    }

    public static boolean test_IF_ICMPGE(int x, int y) {
        if (x < y) {
            return false;
        }

        return true;
    }

    public static int test_IINC(int x, int sign) {
        if (sign < 0) {
            return --x;
        } else {
            return ++x;
        }
    }

    public static int test_IMUL(int x, int y) {
        return x * y;
    }

    public static int test_INEG(int x) {
        return -x;
    }

    public static int test_IOR(int x, int y) {
        return x | y;
    }

    public static int test_ISHL1(int x) {
        return x << 1;
    }

    public static int test_ISHL10(int x) {
        return x << 10;
    }

    public static int test_ISHLm3(int x) {
        return x << -3;
    }

    public static int test_ISHR1(int x) {
        return x >> 1;
    }

    public static int test_ISHR10(int x) {
        return x >> 10;
    }

    public static int test_ISHRm3(int x) {
        return x >> -3;
    }

    public static int test_ISUB(int x, int y) {
        return x - y;
    }

    public static int test_IUSHR1(int x) {
        return x >>> 1;
    }

    public static int test_IXOR(int x, int y) {
        return x ^ y;
    }
}
