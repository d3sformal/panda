package gov.nasa.jpf.abstraction;


public class Example {

  public static void main (String[] args) {
	  test(0,0);
  }
  /* we want to let the user specify that this method should be abstract */

  static void test (int x, int y) {
	  x = x + y;
	  if (x < 0)
		  System.out.println("x le 0");
	  else
		  System.out.println("x ge 0");
  }
}

