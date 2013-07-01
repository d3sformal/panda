package ambiguity;

class A
{
    B x;

    public A() {
        x = new B();
    }
}

class B
{
    C y;

    public B() {
        y = new C();
    }
}

class C
{
    int z;
}

public class Ambiguity
{
    static A static_a = new A();
    static B static_b;
    static C static_c = new C();

    public static void main(String[] args) {
        A a = new A();
        B b;
        C c = new C();

        // LOCAL / HEAP

        a.x.y.z = 0;
        c.z = 3;

        b = a.x;
        a.x.y = c;

        // STATIC

        static_a.x.y.z = 0;
        static_c.z = 3;

        static_b = static_a.x;
        static_a.x.y = static_c;
    }
}
