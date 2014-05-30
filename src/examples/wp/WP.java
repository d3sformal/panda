package wp;

class A {
    int f;
}

public class WP {
    public static void main(String[] args) {
        A a = new A();

        a.f = 3;

        a = new A();
    }
}
