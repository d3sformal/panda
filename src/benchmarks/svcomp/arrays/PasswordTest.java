package svcomp.arrays;

// Taken from SVCOMP (ETAPS 2014)

public class PasswordTest {
    private static final int SIZE = 5;

    public static void test() {
        int[] password = new int[SIZE];
        int[] guess = new int[SIZE];

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
