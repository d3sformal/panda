package gov.nasa.jpf.abstraction;

public class IntBC {

	public static void main(String[] args) {		
// Uncomment to test specific bytecode		
		test_TABLESWITCH();
		test_LOOKUPSWITCH();
		test_I2F(1);
		test_I2D(1);
		test_I2L(1);
		
		test_IADD(3, -2);
		test_IADD(3, 1);
		test_IADD(-1, 0);
		
		test_IAND(3, -1);
		test_IAND(3, 1);
		test_IAND(-3, 0);		
		
		test_IDIV(3, -1);
		test_IDIV(-3, 1);
		test_IDIV(-3, -1);
		test_IDIV(0, -1);		

		test_IF_ICMPEQ(3, -1);
		test_IF_ICMPNE(3, -1);
		test_IF_ICMPLT(3, -1);
		test_IF_ICMPLE(1, 0);
		test_IF_ICMPGT(1, 0);
		test_IF_ICMPGE(1, 0);
		
		test_IINC(-2, +1);
		test_IINC(0, +1);
		test_IINC(0, -1);
		test_IINC(2, -1);		
		
		test_IMUL(3, -1);
		test_IMUL(-3, 1);
		test_IMUL(-3, -2);
		test_IMUL(2, -1);	
		
		test_INEG(-3);
		test_INEG(0);
		test_INEG(3);
		
		test_IOR(3, -1);
		test_IOR(3, 1);
		test_IOR(-1, 0);	
		
		test_IREM(3, -2);
		test_IREM(-3, 2);
		test_IREM(-3, -1);
		test_IREM(0, -1);	
		
		test_ISHL(1, 1);
		test_ISHL(0, 10);
		test_ISHL(1, -3);
		test_ISHL(-1, -3);		
		
		test_ISHR(1, 1);
		test_ISHR(0, 10);
		test_ISHR(1, -3);
		test_ISHR(-1, -3);		
		
		test_ISUB(3, -1);
		test_ISUB(3, 1);
		test_ISUB(3, 7);		
		test_ISUB(-1, 0);
		
		test_ISHR(1, 1);
		test_ISHR(0, 10);
		test_ISHR(1, -3);
		test_ISHR(-1, -3);
		
		test_IXOR(3, -1);
		test_IXOR(3, 1);
		test_IXOR(-1, 0);
	}

	public static void test_TABLESWITCH() {
		System.out.println("\n===== TABLESWITCH =====");
		int n = 1;
		System.out.println("Choose " + n + " from {-2, -1, 0, 1, 2}");
		n = Debug.makeAbstractInteger(n);
		switch (n) {
		case -2:
			System.out.println("-2 is chosen");
			break;
		case -1:
			System.out.println("-1 is chosen");
			break;
		case 0:
			System.out.println("0 is chosen");
			break;
		case 1:
			System.out.println("1 is chosen");
			break;
		case 200:
			System.out.println("2 is chosen");
			break;			
		default:
			System.out.println("default branch is chosen");
		}			
	}
	
	public static void test_LOOKUPSWITCH() {
		System.out.println("\n===== LOOKUPSWITCH =====");
		int n = 1;
		System.out.println("Choose " + n +" from {-200, -1, 0, 1, 200}");
		n = Debug.makeAbstractInteger(n);
		switch (n) {
		case -200:
			System.out.println("-200 is chosen");
			break;
		case -1:
			System.out.println("-1 is chosen");
			break;
		case 0:
			System.out.println("0 is chosen");
			break;
		case 1:
			System.out.println("1 is chosen");
			break;
		case 200:
			System.out.println("200 is chosen");
			break;			
		default:
			System.out.println("default branch is chosen");
		}		
	}
	
	public static void test_I2F(int x) {
		System.out.println("\n===== I2F =====");
		x = Debug.makeAbstractInteger(x);
		float y = 0.0f;
		y = Debug.makeAbstractFloat(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractFloat(y));
		System.out.println("y = x");
		y = x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractFloat(y));
	}		
	
	public static void test_I2D(int x) {
		System.out.println("\n===== I2D =====");
		x = Debug.makeAbstractInteger(x);
		double y = 0.0;
		y = Debug.makeAbstractDouble(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractDouble(y));
		System.out.println("y = x");
		y = x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractDouble(y));
	}	
	
	public static void test_I2L(int x) {
		System.out.println("\n===== I2L =====");
		x = Debug.makeAbstractInteger(x);
		long y = 0;
		y = Debug.makeAbstractLong(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractLong(y));
		System.out.println("y = x");
		y = x;
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractLong(y));
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

	public static void test_IF_ICMPEQ(int x, int y) {
		System.out.println("\n===== IF_ICMPEQ =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		// boolean condition down here is proper
		System.out.printf("x == y is %s\n", (x != y) ? "false" : "true");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}
	
	public static void test_IF_ICMPNE(int x, int y) {
		System.out.println("\n===== IF_ICMPNE =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		// boolean condition down here is proper
		System.out.printf("x != y is %s\n", (x == y) ? "false" : "true");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}		
	
	public static void test_IF_ICMPLT(int x, int y) {
		System.out.println("\n===== IF_ICMPLT =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x < y is %s\n", (x >= y) ? "false" : "true");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IF_ICMPLE(int x, int y) {
		System.out.println("\n===== IF_ICMPLE =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x <= y is %s\n", (x > y) ? "false" : "true");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IF_ICMPGT(int x, int y) {
		System.out.println("\n===== IF_ICMPGT =====");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x > y is %s\n", (x <= y) ? "false" : "true");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IF_ICMPGE(int x, int y) {
		System.out.println("\n===== IF_ICMPGE =====\n");
		x = Debug.makeAbstractInteger(x);
		y = Debug.makeAbstractInteger(y);

		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
		System.out.printf("x >= y is %s\n", (x < y) ? "false" : "true");
		System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
				Debug.getAbstractInteger(y));
	}

	public static void test_IINC(int x, int sign) {
		System.out.println("\n===== IINC =====\n");
		x = Debug.makeAbstractInteger(x);

		System.out.printf("x is %s\n", Debug.getAbstractInteger(x));
		if (sign < 0) {
			System.out.println("--x");
			--x;
		} else {
			System.out.println("++x");
			++x;			
		}
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
		System.out.println("\n===== IUSHR =====");
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
