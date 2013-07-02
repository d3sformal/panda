package array;

class A {
	int f;
}

public class Array {
	public static void main(String[] args) {
		A a[] = new A[1];
		a[0] = new A();
		int i = a[0].f;
		
		System.out.println(i);
	}
}
