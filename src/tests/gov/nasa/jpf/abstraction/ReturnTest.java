package gov.nasa.jpf.abstraction;

public class ReturnTest extends BaseTest {
    public static void main(String[] args) {
        Return r = Return.getValue();

        assertConjunction("r.x = 42: true");
    }
}

class Return {
    int x;

    static Return getValue() {
        Return ret = new Return();

        ret.x = 42;

        return ret;
    }
}
