package svcomp.recursive;

import gov.nasa.jpf.abstraction.Verifier;

public class GreatestCommonDenominator02TrueUnreachableLabel {
    private static int gcd(int y1, int y2) {
        assert y1 > 0 && y2 > 0;

        if (y1 == y2) {
            return y1;
        } else if (y1 > y2) {
            return gcd(y1 - y2, y2);
        } else {
            return gcd(y1, y2 - y1);
        }
    }

    private static boolean divides(int n, int m) {
        if (m == 0) {
            return true;
        } else if (n > m) {
            return false;
        } else {
            return divides(n, m - n);
        }
    }

    public static void main(String[] args) {
        int m = Verifier.unknownInt();
        int n = Verifier.unknownInt();

        if (m <= 0 || m > 2147483647) return;
        if (n <= 0 || n > 2147483647) return;

        int z = gcd(m, n);

        assert divides(z, m);
    }

}
