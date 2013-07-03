package array;

class A {
	int f;
}

public class Array {
	static A static_a[] = new A[2];
	
	static {
		static_a[0] = new A();
		static_a[1] = new A();
	}
	
	public static void main(String[] args) {
		A a[] = static_a;
		int i = a[0].f;
		
		System.out.println(i);
	}
}
