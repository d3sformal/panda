package gov.nasa.jpf.abstraction.numeric;

public class DoubleBC {

    public static void main(String[] args) {
//        test_D2F(1);
//        test_D2I(1);
//        test_D2L(1);
        test_DADD(1, -1);
//        test_DDIV(1, 1);
//        test_DCMP(1, 0);
//        test_DMUL(1, 0);
//        test_DNEG(1);
//        test_DREM(1, 1);
//        test_DSUB(1, 0);
    }

    public static void test_D2F(double x) {
        System.out.println("\n===== D2F =====");
        x = Debug.makeAbstractDouble(x);
        float y = 0;
        y = Debug.makeAbstractFloat(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractFloat(y));
        System.out.println("y = x");
        y = (float) x;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractFloat(y));
    }

    public static void test_D2I(double x) {
        System.out.println("\n===== D2I =====");
        x = Debug.makeAbstractDouble(x);
        int y = 0;
        y = Debug.makeAbstractInteger(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractInteger(y));
        System.out.println("y = x");
        y = (int) x;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractInteger(y));
    }

    public static void test_D2L(double x) {
        System.out.println("\n===== D2L =====");
        x = Debug.makeAbstractDouble(x);
        long y = 0;
        y = Debug.makeAbstractLong(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractLong(y));
        System.out.println("y = x");
        y = (long) x;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractLong(y));
    }

    public static void test_DADD(double x, double y) {
        System.out.println("\n===== DADD =====");
        x = Debug.makeAbstractDouble(x);
        y = Debug.makeAbstractDouble(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
        System.out.println("y = x + y");
        y = x + y;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
    }

    public static void test_DCMP(double x, double y) {
        System.out.println("\n===== DCMPG =====");
        x = Debug.makeAbstractDouble(x);
        y = Debug.makeAbstractDouble(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
        System.out.printf("x > y is %s\n", (x > y) ? "true" : "false");
        System.out.printf("x >= y is %s\n", (x >= y) ? "true" : "false");
        System.out.printf("x == y is %s\n", (x == y) ? "true" : "false");
        System.out.printf("x < y is %s\n", (x < y) ? "true" : "false");
        System.out.printf("x <= y is %s\n", (x <= y) ? "true" : "false");
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
    }

    public static void test_DCMPL(double x, double y) {
        System.out.println("\n===== DCMPL =====");
        x = Debug.makeAbstractDouble(x);
        y = Debug.makeAbstractDouble(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
        System.out.println("y = x + y");
        y = x + y;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
    }

    public static void test_DDIV(double x, double y) {
        System.out.println("\n===== DDIV =====");
        x = Debug.makeAbstractDouble(x);
        y = Debug.makeAbstractDouble(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
        System.out.println("y = x / y");
        y = x / y;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
    }

    public static void test_DMUL(double x, double y) {
        System.out.println("\n===== DMUL =====");
        x = Debug.makeAbstractDouble(x);
        y = Debug.makeAbstractDouble(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
        System.out.println("y = x * y");
        y = x * y;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
    }

    public static void test_DNEG(double x) {
        System.out.println("\n===== DNEG =====");
        x = Debug.makeAbstractDouble(x);

        System.out.printf("x is %s\n", Debug.getAbstractDouble(x));
        x = -x;
        System.out.printf("x is %s\n", Debug.getAbstractDouble(x));
    }

    public static void test_DREM(double x, double y) {
        System.out.println("\n===== DREM =====");
        x = Debug.makeAbstractDouble(x);
        y = Debug.makeAbstractDouble(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
        System.out.println("y = x % y");
        y = x % y;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
    }

    public static void test_DSUB(double x, double y) {
        System.out.println("\n===== DSUB =====");
        x = Debug.makeAbstractDouble(x);
        y = Debug.makeAbstractDouble(y);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
        System.out.println("y = x - y");
        y = x - y;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractDouble(x),
                Debug.getAbstractDouble(y));
    }

}
