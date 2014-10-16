package gov.nasa.jpf.abstraction;

public class FineGrainedAbstractionTest extends BaseTest {
    @Test
    public static void test() {
        int x = 0;

        assert x >= 0;
    }
}
