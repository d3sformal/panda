package methods;

public class Methods {
	
	int a;
	int b;
	
	public void do1() {
		a = 1;
		b = do2(a);
	}
	
	public int do2(int c) {
		c++;
		
		return c;
	}

	public static void main(String[] args) {
		Methods m = new Methods();
		
		m.do1();
	}

}
