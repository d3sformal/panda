package gov.nasa.jpf.abstraction.predicate;

import org.junit.Test;

public class TestTest extends BaseTest {
    public static void main (String[] args) {
        int x = 3;
        int y = 5;

        assertConjunction("x = 3: true");

        x = 4;

        assertDisjunction("x = 4: unknown", "0 = 0: false");

        assertAliased("a.b.c.d", "e.f.g"); // Exactly the same sets ...
        //assertNotAliased("x", "y"); // Completely different sets of looked up values

        //assertNumberOfPossibleValues("a.b.c.d[10].e", 8);

        //...
    }
}
