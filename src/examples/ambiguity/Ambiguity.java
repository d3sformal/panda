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
    public static void main(String[] args) {
        A a = new A();
        B b;
        C c = new C();

        a.x.y.z = 0;
        c.z = 3;

        b = a.x;
        a.x.y = c;
    }
}
