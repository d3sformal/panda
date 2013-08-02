package diamond;

class A {
	B b1;
	B b2;
}

class B {
	C c;
	D d;
}

class C {
	int f;
}

class D {
	int f;
}

public class Diamond {
	private static int i = 0;

	public static void main(String[] args) {
		A a = getA();
		
		B b = getB();
		b.c = a.b1.c;
		
		a.b2 = b;
	}
	
	public static A getA() {
		A a = new A();
		
		a.b1 = getB();
		a.b2 = getB();
		
		return a;
	}
	
	public static B getB() {
		B b = new B();
		
		b.c = getC();
		b.d = getD();
		
		return b;
	}
	
	public static C getC() {
		C c = new C();
		
		++i;
		c.f = i;
		
		return c;
	}
	
	public static D getD() {
		D d = new D();
		
		++i;
		d.f = i;
		
		return d;
	}
}
