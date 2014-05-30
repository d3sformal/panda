package statics;

class A {
    int x = 10;
}

public class Statics {

    static int x = 1;
    static A a = new A();

    public static void main(String[] args) {
        statics.Statics.x = 10;
        statics.Statics.a.x = x;
    }

}
