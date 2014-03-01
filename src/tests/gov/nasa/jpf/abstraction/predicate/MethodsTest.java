package gov.nasa.jpf.abstraction.predicate;

public class MethodsTest extends BaseTest {
	
	int a;
	int b;
	int x;
	static int C = 2;
	
	public MethodsTest() {
		a = 7;
	}
	
	public void do1() {
		int c = 2;
		
		a = 1;
		b = do2(c + 1, a) + 1;

		assertConjunction("b = 6: true", "a = -10: true", "x = 1: false");
	}
	
	public int do2(int c, int d) {
		a = -10;
		x = C;
		c++;
		
		return c + d;
	}

	public static void main(String[] args) {
		MethodsTest m = new MethodsTest();
		
		m.do1();
	}

}
