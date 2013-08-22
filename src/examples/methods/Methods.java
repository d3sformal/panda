package methods;

public class Methods {
	
	int a;
	int b;
	int x;
	
	public void do1() {
		int c = 2;
		
		a = 1;
		b = do2(c + 1, a) + 1;
	}
	
	public int do2(int c, int d) {
		a = -10;
		x = 2;
		c++;
		
		return c + d;
	}

	public static void main(String[] args) {
		Methods m = new Methods();
		
		m.do1();
	}

}
