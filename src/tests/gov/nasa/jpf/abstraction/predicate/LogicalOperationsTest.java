package gov.nasa.jpf.abstraction.predicate;

public class LogicalOperationsTest extends BaseTest {
    @FailingTest
    public static void testAND1() {
        int o = 1;
        int t = 2;
        int error = t & o;
    }

    @Test
    public static void testAND2() {
        int z = 0;
        int o = 1;
        int zz = (z & z);
        int zo = (z & o);
        int oz = (o & z);
        int oo = (o & o);
    }

    @FailingTest
    public static void testOR1() {
        int z = 0;
        int t = 2;
        int error = (t | z);
    }

    @Test
    public static void testOR2() {
        int z = 0;
        int o = 1;
        int zz = (z | z);
        int zo = (z | o);
        int oz = (o | z);
        int oo = (o | o);
    }

    @FailingTest
    public static void testXOR1() {
        int z = 0;
        int t = 2;
        int error = (t ^ z);
    }

    @Test
    public static void testXOR2() {
        int z = 0;
        int o = 1;
        int zz = (z ^ z);
        int zo = (z ^ o);
        int oz = (o ^ z);
        int oo = (o ^ o);
    }
}
