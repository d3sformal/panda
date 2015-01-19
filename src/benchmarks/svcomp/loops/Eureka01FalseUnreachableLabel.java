package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class Eureka01FalseUnreachableLabel {
    private static int INFINITY = 899;

    public static void main(String[] args) {
        int nodecount = Verifier.unknownInt();
        int edgecount = Verifier.unknownInt();

        if (0 > nodecount || nodecount > 5) return;
        if (0 > edgecount || edgecount > 20) return;

        int source = 0;
        int[] Source = new int[] {0, 4, 1, 1, 0, 0, 1, 3, 4, 4, 2, 2, 3, 0, 0, 3, 1, 2, 2, 3};
        int[] Dest = new int[] {1, 3, 4, 1, 1, 4, 3, 4, 3, 0, 0, 0, 0, 2, 3, 0, 2, 1, 0, 4};
        int[] Weight = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        int[] distance = new int[5];

        for (int i = 0; i < nodecount; i++) {
            if (i == source) {
                distance[i] = 0;
            } else {
                distance[i] = INFINITY;
            }
        }

        for (int i = 0; i < nodecount; i++) {
            for (int j = 0; j < edgecount; j++) {
                int x = Dest[j];
                int y = Source[j];

                if (distance[x] > distance[y] + Weight[j]) {
                    distance[x] = -1;
                }
            }
        }

        for (int i = 0; i < edgecount; i++) {
            int x = Dest[i];
            int y = Source[i];

            if (distance[x] > distance[y] + Weight[i]) {
                return;
            }
        }

        for (int i = 0; i < nodecount; i++) {
            assert distance[i] >= 0;
        }
    }

}

