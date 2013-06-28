package ambiguity;

class A
{
  B x;
}

class B
{
  int y;
}

public class Ambiguity
{
    public static void main(String[] args) {
        A a = new A();
        B b = a.x;

        b.y = 3;
    }
}
