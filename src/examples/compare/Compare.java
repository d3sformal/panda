package compare;

public class Compare {

	public static void main(String[] args) {
		int a, b;
		int x, y = 0;
		
		a = 3;
		b = 2;
		
		if (a * b < 12) {
			x = 0;
		} else {
			y = 1;
		}
		
		int i, j, k;
		
		i = 10;
		j = 100;
		k = 10;
		
		if (i < j) {
			x = 2;
		} else {
			y = 3;
		}

		x = y; // force the compiler to keep the variable 'y'
	}

}
