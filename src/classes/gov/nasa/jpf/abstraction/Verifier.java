package gov.nasa.jpf.abstraction;

/**
 * API that can be used in the analyzed program (SuT).
 */
public class Verifier
{
    public static int unknownInt()
    {
        return 0;
    }

    public static int unknownPositiveInt()
    {
        return 2;
    }

    public static char unknownChar()
    {
        return 0;
    }

    public static boolean unknownBool()
    {
        return false;
    }
}
