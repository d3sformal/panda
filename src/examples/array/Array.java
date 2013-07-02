package array;

class A {
	int f;
}

public class Array {
	private static int LENGTH;

	public static void main(String[] args) {
		LENGTH = 1;

		A x[] = new A[LENGTH];
		int y[] = new int[LENGTH];

		for (int i = 0; i < LENGTH; ++i) {
			x[i] = new A();

			if (i % 2 == 0) {
				x[i].f = y[i];
			} else {
				y[i] = x[i].f;
			}
		}
	}
}
