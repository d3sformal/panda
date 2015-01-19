package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class TREX03TrueUnreachableLabel {
    public static void main(String[] args) {
        int x1 = Verifier.unknownInt();
        int x2 = Verifier.unknownInt();
        int x3 = Verifier.unknownInt();

        if (x1 < 0 || x2 < 0 || x3 < 0) return;

        int d1 = 1;
        int d2 = 1;
        int d3 = 1;

        boolean c1 = Verifier.unknownBool();
        boolean c2 = Verifier.unknownBool();

        while (x1 > 0 && x2 > 0 && x3 > 0) {
            if (c1) {
                x1 = x1 - d1;
            } else if (c2) {
                x2 = x2 - d2;
            } else {
                x3 = x3 - d3;
            }

            c1 = Verifier.unknownBool();
            c2 = Verifier.unknownBool();
        }

        assert x1 == 0 || x2 == 0 || x3 == 0;
    }

}

