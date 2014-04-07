package svcomp.recursive;

public class Ackermann01TrueUnreachableLabel {
    private static int ackermann(int m, int n) {
        if (m == 0) {
            return n + 1;
        } else if (n == 0) {
            return ackermann(m - 1, 1);
        } else {
            return ackermann(m - 1, ackermann(m, n - 1));
        }
    }

    public static void main(String[] args) {
        int m = unknown();
        int n = unknown();

        if (m < 0 || m > 3) return;
        if (n < 0 || n > 23) return;

        int result = ackermann(m, n);

        assert result >= 0;
    }

    private static int unknown() {
        return 0;
    }
}
