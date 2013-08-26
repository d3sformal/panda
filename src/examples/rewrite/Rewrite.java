package rewrite;

public class Rewrite {
	Rewrite x;
	
	public static void main(String[] args) {
		Rewrite r[] = {new Rewrite(), new Rewrite()};
		
		r[0].x = new Rewrite();
		r[1] = r[0];
	}
}
