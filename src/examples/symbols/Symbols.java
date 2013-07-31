package symbols;

class A {
	B b;
}

class B {
	int i;
}

public class Symbols {

	public static void main(String[] args) {
		A x[] = new A[] { new A(), new A() };
		A y = new A();
		A z = new A();
		A w[][] = new A[2][2];
		w[0] = x;
		
		x[0].b = new B();
		x[0].b.i = 1;
		
		x[1].b = new B();
		x[1].b.i = 2;
		
		y.b = new B();
		y.b.i = 3;

		x[0].b = y.b;
		
		y = z;
		x = new A[10];
	}

}
