package anonymousObject;

public class AnonymousObject {
	int i;
	
	public static void main(String[] args) {
		AnonymousObject o = new AnonymousObject();
		
		o.i = 10;
		
		o = new AnonymousObject();
	}
}
