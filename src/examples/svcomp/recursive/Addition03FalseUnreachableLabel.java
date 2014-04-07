package svcomp.recursive;

public class Addition03FalseUnreachableLabel {
    private static int addition(int m, int n) {
        if (n == 0) {
            return m;
        } else if (n > 0) {
            return addition(m + 1, n - 1);
        } else {
            return addition(m - 1, n + 1);
        }
    }

    public static void main(String[] args) {
        int m = unknown();
        int n = unknown();

        int result = addition(m, n);

        assert m < 100 || n < 100 || result >= 200;
    }

    private static int unknown() {
        return 0;
    }
}
