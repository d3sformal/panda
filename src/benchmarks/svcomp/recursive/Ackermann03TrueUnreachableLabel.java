package svcomp.recursive;

import gov.nasa.jpf.abstraction.Verifier;

public class Ackermann03TrueUnreachableLabel {
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
        int m = Verifier.unknownInt();
        int n = Verifier.unknownInt();

        if (m < 0 || m > 3) return;
        if (n < 0 || n > 23) return;

        int result = ackermann(m, n);

        assert m != 3 || n != 2 || result == 7;
    }

}
