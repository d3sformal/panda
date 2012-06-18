package gov.nasa.jpf.abstraction;


public class Example {

  public static void main (String[] args) {
	  test_IADD(2,2);
	  test_IMUL(-2,-2);
	  test_INEG(-2);
	  test_IDIV(5,-1);
  }
  /* we want to let the user specify that this method should be abstract */

  static void test_IADD (int x, int y) {
	  System.out.println("\n=== IADD dirty debuging ===\n");
	  x = Debug.makeAbstractInteger(x);
	  y = Debug.makeAbstractInteger(y);
	  
	  System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x), 
			  Debug.getAbstractInteger(y));	  
	  y = x + y;
	  System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x), 
			  Debug.getAbstractInteger(y));  
  }
  
  static void test_IMUL(int x, int y) {
	  System.out.println("\n=== IMUL dirty debuging ===\n");
	  x = Debug.makeAbstractInteger(x);
	  y = Debug.makeAbstractInteger(y);	  
	  
	  System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x), 
			  Debug.getAbstractInteger(y));	  
	  y = x * y;
	  System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x), 
			  Debug.getAbstractInteger(y));	  
  }
  
  static void test_IDIV(int x, int y) {
	  System.out.println("\n=== IDIV dirty debuging ===\n");
	  x = Debug.makeAbstractInteger(x);
	  y = Debug.makeAbstractInteger(y);	  
	  
	  System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x), 
			  Debug.getAbstractInteger(y));	  
	  y = x / y;
	  System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x), 
			  Debug.getAbstractInteger(y));	  
  }
  
  static void test_INEG(int x) {
	  System.out.println("\n=== INEG dirty debuging ===\n");
	  x = Debug.makeAbstractInteger(x);	  
	  
	  System.out.printf("x is %s\n", Debug.getAbstractInteger(x));	  
	  x = -x;	  
	  System.out.printf("x is %s\n", Debug.getAbstractInteger(x));	  
  }
}

