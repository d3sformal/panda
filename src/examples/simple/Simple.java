package simple;

public class Simple {

    Simple x;

    public static void main(String[] args) {
        int i = 0;

        ++i;
        ++i;

        Simple s = new Simple();
        s.x = s;//new Simple();

        System.err.println(i);
    }

}
