package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class VogalFalseUnreachableLabel {
    private static int MAX = 10;

    public static void main(String[] args) {
        char[] inputString = new char[MAX];
        char[] vogalArray = new char[] {'a', 'A', 'e', 'E', 'i', 'I', 'o', 'O', 'u', 'U', '\0'};

        for (int i = 0; i < MAX; i++) {
            inputString[i] = Verifier.unknownChar();
        }

        inputString[MAX - 1] = '\0';

        int nCaracter = 0;

        while (inputString[nCaracter] != '\0') {
            nCaracter++;
        }

        int cont = 0;

        for (int i = 0; i < nCaracter; i++) {
            for (int j = 0; j < 8; j++) {
                if (inputString[i] == vogalArray[j]) {
                    cont++;
                }
            }
        }

        int i = 0;
        int contAux = 0;

        while (inputString[i] != '\0') {
            for (int j = 0; j < 10; j++) {
                if (inputString[i] == vogalArray[j]) {
                    contAux++;
                }
            }

            i++;
        }

        assert contAux == cont;
    }

}
