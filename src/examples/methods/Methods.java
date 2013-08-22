package methods;

public class Methods {
	
	int a;
	int b;
	int x;
	static int C = 2; //WONT WORK ... static <init> returns -> drops all info
	
	public void do1() {
		int c = 2;
		
		a = 1;
		b = do2(c + 1, a) + 1;
	}
	
	public int do2(int c, int d) {
		a = -10;
		x = C;
		c++;
		
		return c + d;
	}

	public static void main(String[] args) {
		C = 2; //SHOULD WORK INSTEAD OF STATIC INIT
		
		Methods m = new Methods();
		
		m.do1();
	}

}
