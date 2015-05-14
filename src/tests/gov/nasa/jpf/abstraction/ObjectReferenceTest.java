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
