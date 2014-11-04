package gov.nasa.jpf.abstraction;

/**
 * API that can be used in the analyzed program (SuT).
 */
public class Verifier {
    native public static int unknownInt();

    public static int unknownPositiveInt() {
        int x = unknownInt();

        return x < 0 ? -x : x;
    }

    public static char unknownChar() {
        return (char) unknownInt();
    }

    public static boolean unknownBool() {
        int x = unknownInt();

        return x < 0;
    }
}
