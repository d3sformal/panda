package gov.nasa.jpf.abstraction.numeric;

public class Debug {


     native public static int makeAbstractInteger(int v);
     native public static double makeAbstractDouble(double v);
     native public static float makeAbstractFloat(float v);
     native public static long makeAbstractLong(long v);

     native public static String getAbstractInteger(int v);
     native public static String getAbstractFloat(float v);
     native public static String getAbstractDouble(double v);
     native public static String getAbstractLong(long v);
     native public static String getAbstractBoolean(boolean v);

}
