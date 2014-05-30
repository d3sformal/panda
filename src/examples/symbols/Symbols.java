package symbols;

class A {
    B b;
}

class B {
    int i;
}

public class Symbols {

    public static void main(String[] args) {
        A k[] = new A[2];
        A l[] = new A[1];
        A m = new A();
        A n = new A();
        A o[][] = new A[2][2];

        k[0] = new A();
        k[1] = new A();

        o[0] = k;

        k[0].b = new B();
        k[0].b.i = 1;

        k[1].b = new B();
        k[1].b.i = 2;

        m.b = new B();
        m.b.i = 3;

        k[0].b = m.b;

        m = n;
        k = new A[10];

        k = l;
    }

}
