package svcomp.recursive;

public class Fibonacci01TrueUnreachableLabel {
    private static int fibonacci(int n) {
        if (n < 1) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return fibonacci(n-1) + fibonacci(n-2);
        }
    }

    public static void main(String[] args) {
        int x = unknown();

        if (x > 46 || x == -2147483648) return;

        int result = fibonacci(x);

        assert result >= x - 1;
    }

    private static int unknown() {
        return 0;
    }
}
