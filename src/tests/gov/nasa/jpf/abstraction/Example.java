package gov.nasa.jpf.abstraction;


public class Example {

  public static void main (String[] args) {
	  test(2,2);
  }
  /* we want to let the user specify that this method should be abstract */

  static void test (int x, int y) {
	  x = Debug.makeAbstractInteger(x);
	  y = Debug.makeAbstractInteger(y);
	  System.out.println("x "+Debug.getAbstractInteger(x) + " y "+
			  Debug.getAbstractInteger(y));
	  y = x + y;
	  System.out.println("x "+Debug.getAbstractInteger(x) + " y "+
	  Debug.getAbstractInteger(y));
	  if (y < 2)
		  System.out.println("x lt 0");
	  else
		  System.out.println("x ge 0");
  }
}

