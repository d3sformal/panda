package gov.nasa.jpf.abstraction;

public class InitialValueTest extends BaseTest {
    public InitialValueTest() {
        config.add("+panda.refinement=true");
    }

    @Test
    public static void test() {
        assert !InitialValues.z;
        assert InitialValues.b == 0;
        assert InitialValues.c == 0;
        assert InitialValues.d == 0;
        assert InitialValues.f == 0;
        assert InitialValues.i == 0;
        assert InitialValues.l == 0;
        assert InitialValues.s == 0;
    }
}

class InitialValues {
    public static boolean z;
    public static byte b;
    public static char c;
    public static double d;
    public static float f;
    public static int i;
    public static long l;
    public static short s;
}
