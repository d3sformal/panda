package gov.nasa.jpf.abstraction.predicate;

class D{public static void main(String[] args) {new IntTest().bootstrap();}}
public class IntTest extends SignsTest {
    @Test
    public static void test1() {
        test_I2F(1);
    }

    @Test
    public static void test2() {
        test_I2D(1);
    }

    @Test
    public static void test3() {
        test_I2L(1);
    }

    @Test
    public static void test4() {
        test_IADD(3, -2);
    }

    @Test
    public static void test5() {
        test_IADD(3, 1);
    }

    //@Test
    public static void test6() {
        test_IADD(-1, 0);
    }

    //@Test
    public static void test7() {
        test_IAND(3, -1);
    }

    //@Test
    public static void test8() {
        test_IAND(3, 1);
    }

    //@Test
    public static void test9() {
        test_IAND(-3, 0);
    }

    //@Test
    public static void test10() {
        test_IDIV(3, -1);
    }

    //@Test
    public static void test11() {
        test_IDIV(-3, 1);
    }

    //@Test
    public static void test12() {
        test_IDIV(-3, -1);
    }

    //@Test
    public static void test13() {
        test_IDIV(0, -1);
    }

    @Test
    public static void test14() {
        test_IF_ICMPEQ(3, -1);
    }

    @Test
    public static void test15() {
        test_IF_ICMPNE(3, -1);
    }

    @Test
    public static void test16() {
        test_IF_ICMPLT(3, -1);
    }

    @Test
    public static void test17() {
        test_IF_ICMPLE(1, 0);
    }

    @Test
    public static void test18() {
        test_IF_ICMPGT(1, 0);
    }

    @Test
    public static void test19() {
        test_IF_ICMPGE(1, 0);
    }

    @Test
    public static void test20() {
        test_IINC(-2, +1);
    }

    @Test
    public static void test21() {
        test_IINC(0, +1);
    }

    @Test
    public static void test22() {
        test_IINC(0, -1);
    }

    @Test
    public static void test23() {
        test_IINC(2, -1);
    }

    @Test
    public static void test24() {
        test_IMUL(3, -1);
    }

    @Test
    public static void test25() {
        test_IMUL(-3, 1);
    }

    @Test
    public static void test26() {
        test_IMUL(-3, -2);
    }

    @Test
    public static void test27() {
        test_IMUL(2, -1);
    }

    @Test
    public static void test28() {
        test_INEG(-3);
    }

    @Test
    public static void test29() {
        test_INEG(0);
    }

    @Test
    public static void test30() {
        test_INEG(3);
    }

    //@Test
    public static void test31() {
        test_IOR(3, -1);
    }

    //@Test
    public static void test32() {
        test_IOR(3, 1);
    }

    //@Test
    public static void test33() {
        test_IOR(-1, 0);
    }

    //@Test
    public static void test34() {
        test_IREM(3, -2);
    }

    //@Test
    public static void test35() {
        test_IREM(-3, 2);
    }

    //@Test
    public static void test36() {
        test_IREM(-3, -1);
    }

    //@Test
    public static void test37() {
        test_IREM(0, -1);
    }

    @Test
    public static void test38() {
        test_ISHL(1, 1);
    }

    @Test
    public static void test39() {
        test_ISHL(0, 10);
    }

    @Test
    public static void test40() {
        test_ISHL(1, -3);
    }

    @Test
    public static void test41() {
        test_ISHL(-1, -3);
    }

    @Test
    public static void test42() {
        test_ISHR(1, 1);
    }

    @Test
    public static void test43() {
        test_ISHR(0, 10);
    }

    @Test
    public static void test44() {
        test_ISHR(1, -3);
    }

    @Test
    public static void test45() {
        test_ISHR(-1, -3);
    }

    @Test
    public static void test46() {
        test_ISUB(3, -1);
    }

    @Test
    public static void test47() {
        test_ISUB(3, 1);
    }

    @Test
    public static void test48() {
        test_ISUB(3, 7);
    }

    @Test
    public static void test49() {
        test_ISUB(-1, 0);
    }

    @Test
    public static void test50() {
        test_ISHR(1, 1);
    }

    @Test
    public static void test51() {
        test_ISHR(0, 10);
    }

    @Test
    public static void test52() {
        test_ISHR(1, -3);
    }

    @Test
    public static void test53() {
        test_ISHR(-1, -3);
    }

    //@Test
    public static void test54() {
        test_IXOR(3, -1);
    }

    //@Test
    public static void test55() {
        test_IXOR(3, 1);
    }

    //@Test
    public static void test56() {
        test_IXOR(-1, 0);
    }

    public static void test_I2F(int x) {
        float y = 0.0f;
        y = x;
    }

    public static void test_I2D(int x) {
        double y = 0.0;
        y = x;
    }

    public static void test_I2L(int x) {
        long y = 0;
        y = x;
    }

    public static void test_IADD(int x, int y) {
        y = x + y;
    }

    public static void test_IAND(int x, int y) {
        y = x & y;
    }

    public static void test_IDIV(int x, int y) {
        y = x / y;
    }

    public static void test_IF_ICMPEQ(int x, int y) {
        if (x != y) {
        }
    }

    public static void test_IF_ICMPNE(int x, int y) {
        if (x == y) {
        }
    }

    public static void test_IF_ICMPLT(int x, int y) {
        if (x >= y) {
        }
    }

    public static void test_IF_ICMPLE(int x, int y) {
        if (x > y) {
        }
    }

    public static void test_IF_ICMPGT(int x, int y) {
        if (x <= y) {
        }
    }

    public static void test_IF_ICMPGE(int x, int y) {
        if (x < y) {
        }
    }

    public static void test_IINC(int x, int sign) {
        if (sign < 0) {
            --x;
        } else {
            ++x;
        }
    }

    public static void test_IMUL(int x, int y) {
        y = x * y;
    }

    public static void test_INEG(int x) {
        x = -x;
    }

    public static void test_IOR(int x, int y) {
        y = x | y;
    }

    public static void test_IREM(int x, int y) {
        y = x % y;
    }

    public static void test_ISHL(int x, int y) {
        y = x >> y;
    }

    public static void test_ISHR(int x, int y) {
        y = x << y;
    }

    public static void test_ISUB(int x, int y) {
        y = x - y;
    }

    public static void test_IUSHR(int x, int y) {
        y = x >>> y;
    }

    public static void test_IXOR(int x, int y) {
        y = x ^ y;
    }
}
