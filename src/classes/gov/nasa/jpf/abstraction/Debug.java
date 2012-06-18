package gov.nasa.jpf.abstraction;

public class Debug {


	 native public static int makeAbstractInteger(int v);
	 native public static double makeAbstractReal(double v);

	 native public static String getAbstractInteger(int v);
	 native public static String getAbstractReal(double v);
	 native public static String getAbstractBoolean(boolean v);

}
