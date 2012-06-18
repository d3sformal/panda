package gov.nasa.jpf.abstraction;

public class IntBC {

	public static void main(String[] args) {
		// test_I2F(1);
		test_I2D(1);
		// test_I2L(1);
		test_IADD(1, 0);
		test_IAND(1, 0);
		test_IDIV(1, 1);
		test_IFGE(1, 0);
		test_IFGT(1, 0);
		test_IFLE(1, 0);
		test_IFLT(1, 0);
		test_IINC(1);
		test_IMUL(1, 0);
		test_INEG(1);
		test_IOR(1, 0);
		test_IREM(1, 1);
		test_ISHL(1, 0);
		test_ISHR(1, 0);
		test_ISUB(1, 0);
		test_IUSHR(1, 0);
		test_IXOR(1, 0);
	}

	public static void test_I2D(int x) {
		System.out.println("\n===== I2D =====");
		x = Debug.makeAbstractInteger(x);
		double y = 0.0;
		y = Debug.makeAbstractReal(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractReal(y));
		System.out.println("y = x");
		y = x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractReal(y));
	}	
	
	public static void test_IADD(int x, int y) {
		System.out.println("\n===== IADD =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x + y");
		y = x + y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IAND(int x, int y) {
		System.out.println("\n===== IAND =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x & y");
		y = x & y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IDIV(int x, int y) {
		System.out.println("\n===== IDIV =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x / y");
		y = x / y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IFGE(int x, int y) {
		System.out.println("\n===== IFGE =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x >= y is %s\n", (x >= y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IFGT(int x, int y) {
		System.out.println("\n===== IFGT =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x > y is %s\n", (x > y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IFLE(int x, int y) {
		System.out.println("\n===== IFLE =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x <= y is %s\n", (x <= y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IFLT(int x, int y) {
		System.out.println("\n===== IFLT =====\n");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x < y is %s\n", (x < y) ? "true" : "false");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IINC(int x) {
		System.out.println("\n===== IINC =====\n");
		x = Debug.makeAbstractInteger(x);

		System.out.printf("x is %s\n", Debug.getAbstractInteger(x));
		++x;
		System.out.printf("x is %s\n", Debug.getAbstractInteger(x));
	}

	public static void test_IMUL(int x, int y) {
		System.out.println("\n===== IMUL =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x * y");
		y = x * y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_INEG(int x) {
		System.out.println("\n===== INEG =====");
		x = Debug.makeAbstractInteger(x);

		System.out.printf("x is %s\n", Debug.getAbstractInteger(x));
		x = -x;
		System.out.printf("x is %s\n", Debug.getAbstractInteger(x));
	}

	public static void test_IOR(int x, int y) {
		System.out.println("\n===== IOR =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x | y");
		y = x | y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IREM(int x, int y) {
		System.out.println("\n===== IREM =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x % y");
		y = x % y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_ISHL(int x, int y) {
		System.out.println("\n===== ISHL =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x >> y");
		y = x >> y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_ISHR(int x, int y) {
		System.out.println("\n===== ISHR =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x << y");
		y = x << y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_ISUB(int x, int y) {
		System.out.println("\n===== ISUB =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x - y");
		y = x - y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IUSHR(int x, int y) {
		System.out.println("\n===== IOR =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x >>> y");
		y = x >>> y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IXOR(int x, int y) {
		System.out.println("\n===== IXOR =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.println("y = x ^ y");
		y = x ^ y;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

}