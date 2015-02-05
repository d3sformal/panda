package gov.nasa.jpf.abstraction;

/**
 * API that can be used in the analyzed program (SuT).
 */
public class Verifier {
    native public static int unknownInt();
    native public static int unknownPositiveInt();
    native public static char unknownChar();
    native public static boolean unknownBool();
}
