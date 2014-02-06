package gov.nasa.jpf.abstraction.predicate;

import org.junit.Test;

public class TestTest extends BaseTest {
    public static void main (String[] args) {
        int x = 3;

        assertConjunction("x = 3: true");

        x = 4;

        assertDisjunction("x = 4: unknown", "0 = 0: false");

        assertAliasing("a.b.c.d", "e.f.g");
        assertAliasing("x", "x");

        //assertNumberOfPossibleValues("a.b.c.d[10].e", 8);

        //...
    }
}
