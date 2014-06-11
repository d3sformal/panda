package gov.nasa.jpf.abstraction;

import static gov.nasa.jpf.abstraction.BaseTest.*;

public class MethodsTest extends BaseTest {
    public static void main(String[] args) {
        Methods m = new Methods();

        m.do1();
    }
}

class Methods {
    int a;
    int b;
    int x;
    static int C = 2;

    public Methods() {
        a = 7;
    }

    public void do1() {
        int c = 2;

        a = 1;
        b = do2(c + 1, a) + 1;

        assertConjunction("this.b >= 6: true", "this.a = 1: false", "class(gov.nasa.jpf.abstraction.Methods).C = 2: true");
    }

    public int do2(int c, int d) {
        a = -10;
        x = C;
        c++;

        return c + d;
    }
}
