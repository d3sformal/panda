package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class ArrayFalseUnreachableLabel {
    public static void main(String[] args) {
        int SIZE = 1;
        int[] array = new int[SIZE];
        int menor = Verifier.unknownInt();

        for (int j = 0; j < SIZE; ++j) {
            array[j] = Verifier.unknownInt();

            if (array[j] <= menor) {
                menor = array[j];
            }
        }

        assert array[0] > menor;
    }

}
