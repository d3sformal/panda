package anonymousobject;

public class AnonymousObject {
	int i;
	
	public static void main(String[] args) {
		AnonymousObject o = new AnonymousObject();
		
		o.i = 10;
		
		o = new AnonymousObject();
		
		int a[] = new int[1];
		a[0] = 10;
		
		a = new int[2];
	}
}
