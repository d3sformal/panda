package gov.nasa.jpf.abstraction.predicate;

import org.junit.Test;

public class ClassATest extends BaseTest {
    public static void main (String[] args) {
        int x = 3;

        ClassATest.assertConjunction("x = 3: true");

        x = 4;

        ClassATest.assertDisjunction("x = 4: unknown", "0 = 0: false");

        //Test.assertAliasing("a.b.c.d", "x");
        //Test.assertNumberOfPossibleValues("a.b.c.d[10].e", 8);

        //...
    }
}
