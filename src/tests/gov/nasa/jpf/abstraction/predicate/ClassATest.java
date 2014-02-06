package gov.nasa.jpf.abstraction.predicate;

public class ClassATest extends BaseTest {
    public static void main(String[] args) {
        invokeOn(ClassA.class);
    }
}

class ClassA {
    public static void main (String[] args) {
        int x = 3;

        ClassATest.assertConjunction("x = 3: true");

        x = 4;

        ClassATest.assertExclusiveDisjunction("x = x: true", "y = y + 1: false");

        ClassATest.assertDisjunction(
            new String[] {
                "x = 3: true"
            },
            new String[] {
                "a = b: false"
            }
        );

        //Test.assertAliasing("a.b.c.d", "x");
        //Test.assertNumberOfPossibleValues("a.b.c.d[10].e", 8);

        //...
    }
}
