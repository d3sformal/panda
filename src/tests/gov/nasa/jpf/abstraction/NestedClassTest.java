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
