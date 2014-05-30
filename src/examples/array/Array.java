package array;

class A {
    int f;
}

public class Array {
    static A static_a[] = new A[2];

    static {
        static_a[0] = new A();
        static_a[1] = new A();
        static_a[0].f = 1;
        static_a[1].f = 2;
        static_a[0] = static_a[1];
    }

    public static void main(String[] args) {
        A a[] = static_a;
        int i = a[0].f;
        i = -1;
        a[0].f = i + 2;
    }
}
