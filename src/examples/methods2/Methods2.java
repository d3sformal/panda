package methods2;

public class Methods2 {
	private int i;
	private static int X;
	
	public void do1 (Methods2 x) {
		i = 2;
		x.i = 3;
		X = 4;
	}
	
	public static void main(String[] args) {
		Methods2 m1 = new Methods2();
		Methods2 m2 = new Methods2();
		
		m1.do1(m2);
	}
}
