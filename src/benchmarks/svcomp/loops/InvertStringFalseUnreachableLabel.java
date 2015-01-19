package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class InvertStringFalseUnreachableLabel {
    public static void main(String[] args) {
        int MAX = Verifier.unknownInt();
        char[] str1 = new char[MAX];
        char[] str2 = new char[MAX];

        for (int i = 0; i < MAX; i++) {
            str1[i] = Verifier.unknownChar();
        }

        str1[MAX - 1] = '\0';

        int j = 0;

        for (int i = MAX - 1; i >= 0; i--) {
            str2[j] = str1[0];
            j++;
        }

        j = MAX - 1;

        for (int i = 0; i < MAX; i++) {
            assert str1[i] == str2[j];
            j--;
        }
    }

}
