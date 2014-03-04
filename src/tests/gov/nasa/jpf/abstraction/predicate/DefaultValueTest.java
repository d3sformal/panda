package gov.nasa.jpf.abstraction.predicate;

public class DefaultValueTest extends BaseTest {
    public static void main(String[] args) {
        DefaultValue v = new DefaultValue();

        assertConjunction("v.b = 0: true", "v.x = 0: true", "v.o = null: true");
    }
}

class DefaultValue {
    boolean b;
    int x;
    Object o;
}
