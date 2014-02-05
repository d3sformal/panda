package gov.nasa.jpf.abstraction.predicate;

public class Test extends BaseTest {
    public static void main(String[] args) {
        invokeOn(ClassA.class);
    }
}

class ClassA {
    public void main (String[] args) {
        //...

        Test.assertConjunction("x = 0: true", "y = 1: false");
        //Test.assertDisjunction(new String[] {"x = 0: true", "y = 1: false"}, new String[] {"a = b: UNKNOWN"});
        //Test.assertAliasing("a.b.c.d", "x");
        //Test.assertNumberOfPossibleValues("a.b.c.d[10].e", 8);

        //...
    }
}
