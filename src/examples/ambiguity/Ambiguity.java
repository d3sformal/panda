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
    int y;
}

public class Ambiguity
{
    public static void main(String[] args) {
        A a = new A();

        a.x.y = 0;

        B b = a.x;

        b.y = 3;
    }
}
