package svcomp.loops;

public class TREX03TrueUnreachableLabel {
    public static void main(String[] args) {
        int x1 = unknownInt();
        int x2 = unknownInt();
        int x3 = unknownInt();

        if (x1 < 0 || x2 < 0 || x3 < 0) return;

        int d1 = 1;
        int d2 = 1;
        int d3 = 1;

        boolean c1 = unknownBool();
        boolean c2 = unknownBool();

        while (x1 > 0 && x2 > 0 && x3 > 0) {
            if (c1) {
                x1 = x1 - d1;
            } else if (c2) {
                x2 = x2 - d2;
            } else {
                x3 = x3 - d3;
            }

            c1 = unknownBool();
            c2 = unknownBool();
        }

        assert x1 == 0 || x2 == 0 || x3 == 0;
    }

    private static int unknownInt() {
        return 0;
    }

    private static boolean unknownBool() {
        return false;
    }
}

