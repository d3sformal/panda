package gov.nasa.jpf.abstraction;

// Taken from CPAchecker doc/example

public class BoundedLoopTest extends BaseTest {
    public BoundedLoopTest() {
        config.add("+panda.refinement=true");
    }
    public static void main(String[] args) {
        int i = 0;
        int a = 0;

        while (true) {
            if (i == 20) {
                break;
            } else {
                i++;
                a++;
            }
        }

        assert a == i;
    }
}
