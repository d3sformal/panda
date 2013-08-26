package methods2;

class A {
	int i;
	
	public void do1 () {
		Methods2.X = 2;
	}
}

public class Methods2 {
	
	static int X = 2;

	public static void main(String[] args) {
		A a = new A();
		
		a.do1();
	}

}
