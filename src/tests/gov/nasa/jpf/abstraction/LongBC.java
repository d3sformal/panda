package gov.nasa.jpf.abstraction;

public class LongBC {

	public static void main(String[] args) {
		test_L2F(1);
		test_L2D(1);
		test_L2I(1);
		test_LADD(1, 0);
		test_LAND(1, 0);
		test_LDIV(1, 1);
		test_LFGE(1, 0);
		test_LFGT(1, 0);
		test_LFLE(1, 0);
		test_LFLT(1, 0);
		test_LINC(1);
		test_LMUL(1, 0);
		test_LNEG(1);
		test_LOR(1, 0);
		test_LREM(1, 1);
		test_LSHL(1, 0);
		test_LSHR(1, 0);
		test_LSUB(1, 0);
		test_LUSHR(1, 0);
		test_LXOR(1, 0);
	}

	public static void test_L2F(long x) {
		System.out.println("\n===== L2F =====");
		x = Debug.makeAbstractLong(x);
		float y = 0.0f;
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x");
		y = x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractFloat(y));
	}		
	
	public static void test_L2D(long x) {
		System.out.println("\n===== L2D =====");
		x = Debug.makeAbstractLong(x);
		double y = 0.0;
		y = Debug.makeAbstractDouble(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractDouble(y));
		System.out.println("y = x");
		y = x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractDouble(y));
	}	
	
	public static void test_L2I(long x) {
		System.out.println("\n===== L2I =====");
		x = Debug.makeAbstractLong(x);
		int y = 0;
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x");
		y = (int)x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractInteger(y));
	}		
	
	public static void test_LADD(long x, long y) {
		System.out.println("\n===== LADD =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x + y");
		y = x + y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LAND(long x, long y) {
		System.out.println("\n===== LAND =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x & y");
		y = x & y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LDIV(long x, long y) {
		System.out.println("\n===== LDIV =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x / y");
		y = x / y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LFGE(long x, long y) {
		System.out.println("\n===== LFGE =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.printf("x >= y is %s\n", (x >= y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LFGT(long x, long y) {
		System.out.println("\n===== LFGT =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.printf("x > y is %s\n", (x > y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LFLE(long x, long y) {
		System.out.println("\n===== LFLE =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.printf("x <= y is %s\n", (x <= y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LFLT(long x, long y) {
		System.out.println("\n===== LFLT =====\n");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.printf("x < y is %s\n", (x < y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LINC(long x) {
		System.out.println("\n===== LINC =====\n");
		x = Debug.makeAbstractLong(x);

		System.out.printf("x is %s\n", Debug.getAbstractLong(x));
		++x;
		System.out.printf("x is %s\n", Debug.getAbstractLong(x));
	}

	public static void test_LMUL(long x, long y) {
		System.out.println("\n===== LMUL =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x * y");
		y = x * y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LNEG(long x) {
		System.out.println("\n===== LNEG =====");
		x = Debug.makeAbstractLong(x);

		System.out.printf("x is %s\n", Debug.getAbstractLong(x));
		x = -x;
		System.out.printf("x is %s\n", Debug.getAbstractLong(x));
	}

	public static void test_LOR(long x, long y) {
		System.out.println("\n===== LOR =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x | y");
		y = x | y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LREM(long x, long y) {
		System.out.println("\n===== LREM =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x % y");
		y = x % y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LSHL(long x, long y) {
		System.out.println("\n===== LSHL =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x >> y");
		y = x >> y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LSHR(long x, long y) {
		System.out.println("\n===== LSHR =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x << y");
		y = x << y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LSUB(long x, long y) {
		System.out.println("\n===== LSUB =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x - y");
		y = x - y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LUSHR(long x, long y) {
		System.out.println("\n===== LOR =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x >>> y");
		y = x >>> y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

	public static void test_LXOR(long x, long y) {
		System.out.println("\n===== LXOR =====");
		x = Debug.makeAbstractLong(x);
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x ^ y");
		y = x ^ y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractLong(x),
				Debug.getAbstractLong(y));
	}

}
