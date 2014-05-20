package gov.nasa.jpf.abstraction.predicate;

public class LogicalOperationsTest extends BaseTest {
    @FailingTest
    public static void testIAND1() {
        int o = 1;
        int t = 2;
        int error = t & o;
    }

    @Test
    public static void testIAND2() {
        int z = 0;
        int o = 1;
        int zz = (z & z);
        int zo = (z & o);
        int oz = (o & z);
        int oo = (o & o);
    }

    @FailingTest
    public static void testIOR1() {
        int z = 0;
        int t = 2;
        int error = (t | z);
    }

    @Test
    public static void testIOR2() {
        int z = 0;
        int o = 1;
        int zz = (z | z);
        int zo = (z | o);
        int oz = (o | z);
        int oo = (o | o);
    }

    @FailingTest
    public static void testIXOR1() {
        int z = 0;
        int t = 2;
        int error = (t ^ z);
    }

    @Test
    public static void testIXOR2() {
        int z = 0;
        int o = 1;
        int zz = (z ^ z);
        int zo = (z ^ o);
        int oz = (o ^ z);
        int oo = (o ^ o);
    }

    @FailingTest
    public static void testLAND1() {
        long o = 1;
        long t = 2;
        long error = t & o;
    }

    @Test
    public static void testLAND2() {
        long z = 0;
        long o = 1;
        long zz = (z & z);
        long zo = (z & o);
        long oz = (o & z);
        long oo = (o & o);
    }

    @FailingTest
    public static void testLOR1() {
        long z = 0;
        long t = 2;
        long error = (t | z);
    }

    @Test
    public static void testLOR2() {
        long z = 0;
        long o = 1;
        long zz = (z | z);
        long zo = (z | o);
        long oz = (o | z);
        long oo = (o | o);
    }

    @FailingTest
    public static void testLXOR1() {
        long z = 0;
        long t = 2;
        long error = (t ^ z);
    }

    @Test
    public static void testLXOR2() {
        long z = 0;
        long o = 1;
        long zz = (z ^ z);
        long zo = (z ^ o);
        long oz = (o ^ z);
        long oo = (o ^ o);
    }
}
