package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class InsertionSortFalseUnreachableLabel {
    public static void main(String[] args) {
        int SIZE = Verifier.unknownNonNegativeInt();
        int[] v = new int[SIZE];

        for (int j = 1; j < SIZE; j++) {
            int key = v[j];
            int i = j - 1;

            while ((i >= 0) && (v[i] > key)) {
                if (i < 2) v[i + 1] = v[i];

                i = i - 1;
            }

            v[i + 1] = key;
        }

        for (int k = 1; k < SIZE; k++) {
            assert v[k - 1] <= v[k];
        }
    }
}
