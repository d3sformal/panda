package paper;

class X {
	int x = 42;
} 

public class Paper {
	public static void main(String[] args) {
		X x = new X();
		
		int i;
		
		while (x.x > 0) {
			i = x.x;
			
			--x.x;
		}
	}
}
