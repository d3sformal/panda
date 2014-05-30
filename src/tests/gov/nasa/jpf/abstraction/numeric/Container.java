package gov.nasa.jpf.abstraction.numeric;

public class Container {

    public static void main(String[] args) {
        System.out.println("\n===== IADD =====");
        int x = Debug.makeAbstractInteger(3);
        int y = Debug.makeAbstractInteger(-2);

        System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
                Debug.getAbstractInteger(y));
        System.out.println("y = x + y");
        y = x + y;
        System.out.printf("x is %s; y is %s\n", Debug.getAbstractInteger(x),
                Debug.getAbstractInteger(y));
    }

}
