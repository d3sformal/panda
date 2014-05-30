package methods;

public class Methods {

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
    }

    public int do2(int c, int d) {
        a = -10;
        x = C;
        c++;

        return c + d;
    }

    public static void main(String[] args) {
        Methods m = new Methods(); // {a = 1;}

        // m.a = 1;

        m.do1();
    }

}
