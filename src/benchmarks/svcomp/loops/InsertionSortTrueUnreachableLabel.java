package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class InsertionSortTrueUnreachableLabel {
    public static void main(String[] args) {
        // Array length cannot be zero (if we plan to write to the array)
        int SIZE = Verifier.unknownPositiveInt();
        int[] v = new int[SIZE];

        for (int j = 1; j < SIZE; j++) {
            int key = v[j];
            int i = j - 1;

            while ((i >= 0) && (v[i] > key)) {
                v[i + 1] = v[i];

                i = i - 1;
            }

            v[i + 1] = key;
        }

        for (int k = 1; k < SIZE; k++) {
            assert v[k - 1] <= v[k];
        }
    }

}
