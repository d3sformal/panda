package symbols;

class A {
	int i;
}

public class Symbols {

	public static void main(String[] args) {
		A a[] = new A[] { new A(), new A() };
		A b = new A();

		a[0].i = 1;
		a[1].i = 2;
		b.i = 3;
		
		a[0] = b;
	}

}
