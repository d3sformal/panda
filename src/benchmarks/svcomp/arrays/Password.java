package svcomp.arrays;

import static gov.nasa.jpf.abstraction.Verifier.unknownInt;

// Taken from SVCOMP (ETAPS 2014)

public class Password {
    private static final int SIZE = 5;

    public static void test() {
        int[] password = new int[SIZE];
        int[] guess = new int[SIZE];

        for (int i = 0; i < SIZE; ++i) {
            password[i] = unknownInt();
            guess[i] = unknownInt();
        }

        boolean result = true;

        for (int i = 0; i < SIZE; ++i) {
            if (password[i] != guess[i]) {
                result = false;
            }
        }

        if (result) {
            for (int j = 0; j < SIZE; ++j) {
                assert password[j] == guess[j];
            }
        }
    }
}
