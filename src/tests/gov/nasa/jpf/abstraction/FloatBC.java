package gov.nasa.jpf.abstraction;

public class FloatBC {

	public static void main(String[] args) {
		test_F2D(1);
		test_F2I(1);
		test_F2L(1);
		test_FADD(1, 0);
		test_FDIV(1, 1);
		// test_FCMP(1, 0);
		test_FMUL(1, 0);
		test_FNEG(1);
		test_FREM(1, 1);
		test_FSUB(1, 0);
	}

	public static void test_F2D(float x) {
		System.out.println("\n===== F2D =====");
		x = Debug.makeAbstractFloat(x);
		double y = 0;
		y = Debug.makeAbstractDouble(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractDouble(y));
		System.out.println("y = x");
		y = (double) x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractDouble(y));
	}	
	
	public static void test_F2I(float x) {
		System.out.println("\n===== F2I =====");
		x = Debug.makeAbstractFloat(x);
		int y = 0;
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x");
		y = (int) x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractInteger(y));
	}
	
	public static void test_F2L(float x) {
		System.out.println("\n===== F2L =====");
		x = Debug.makeAbstractFloat(x);
		long y = 0;
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x");
		y = (long) x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractLong(y));
	}	

	public static void test_FADD(float x, float y) {
		System.out.println("\n===== FADD =====");
		x = Debug.makeAbstractFloat(x);
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x + y");
		y = x + y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
	}

	public static void test_FCMP(float x, float y) {
		System.out.println("\n===== FCMPG =====");
		x = Debug.makeAbstractFloat(x);
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
		System.out.printf("x > y is %s\n", (x > y) ? "true" : "false");
		System.out.printf("x >= y is %s\n", (x >= y) ? "true" : "false");
		System.out.printf("x == y is %s\n", (x == y) ? "true" : "false");
		System.out.printf("x < y is %s\n", (x < y) ? "true" : "false");
		System.out.printf("x <= y is %s\n", (x <= y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
	}

	public static void test_FCMPL(float x, float y) {
		System.out.println("\n===== FCMPL =====");
		x = Debug.makeAbstractFloat(x);
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x + y");
		y = x + y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
	}

	public static void test_FDIV(float x, float y) {
		System.out.println("\n===== FDIV =====");
		x = Debug.makeAbstractFloat(x);
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x / y");
		y = x / y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
	}

	public static void test_FMUL(float x, float y) {
		System.out.println("\n===== FMUL =====");
		x = Debug.makeAbstractFloat(x);
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x * y");
		y = x * y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
	}

	public static void test_FNEG(float x) {
		System.out.println("\n===== FNEG =====");
		x = Debug.makeAbstractFloat(x);

		System.out.printf("x is %s\n", Debug.getAbstractFloat(x));
		x = -x;
		System.out.printf("x is %s\n", Debug.getAbstractFloat(x));
	}

	public static void test_FREM(float x, float y) {
		System.out.println("\n===== FREM =====");
		x = Debug.makeAbstractFloat(x);
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x % y");
		y = x % y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
	}

	public static void test_FSUB(float x, float y) {
		System.out.println("\n===== FSUB =====");
		x = Debug.makeAbstractFloat(x);
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x - y");
		y = x - y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractFloat(x),
				Debug.getAbstractFloat(y));
	}

}
