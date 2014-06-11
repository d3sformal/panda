package gov.nasa.jpf.abstraction;

// An example of a test driver replacing Junit automatic invocation
// Completely optional
class AssertTestDriver {
    public static void main(String[] args) {
        new AssertTest().bootstrap();
    }
}

public class AssertTest extends BaseTest {
    public AssertTest() {
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker");
    }

    @Test
    public static void test1() {
        int x = 3;
        int y = 5;

        assertConjunction("x = 3: true");

        x = 4;

        assertDisjunction("x = 4: unknown", "0 = 0: false");

        assertAliased("a.b.c.d", "e.f.g"); // Exactly the same sets ...
        assertNotAliased("x", "y"); // Completely different sets of looked up values

        assertNumberOfPossibleValues("a.b.c.d[10].e", 0);
        assertNumberOfPossibleValues("x", 1);

        //...
    }
}
