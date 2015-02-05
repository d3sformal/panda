package svcomp.loops;

import gov.nasa.jpf.abstraction.Verifier;

public class Eureka01FalseUnreachableLabel {
    private static int INFINITY = 899;

    public static void main(String[] args) {
        int nodecount = Verifier.unknownInt();
        int edgecount = Verifier.unknownInt();

        if (0 > nodecount || nodecount > 5) return;
        if (0 > edgecount || edgecount > 4) return;

        int source = 0;
        int[] Source = new int[20];
        int[] Dest = new int[20];
        int[] Weight = new int[20];
        int[] distance = new int[5];

        Source[ 0] = 0; Dest[ 0] = 1; Weight[ 0] =  0;
        Source[ 1] = 4; Dest[ 1] = 3; Weight[ 1] =  1;
        Source[ 2] = 1; Dest[ 2] = 4; Weight[ 2] =  2;
        Source[ 3] = 1; Dest[ 3] = 1; Weight[ 3] =  3;
        /*
        Source[ 4] = 0; Dest[ 4] = 1; Weight[ 4] =  4;
        Source[ 5] = 0; Dest[ 5] = 4; Weight[ 5] =  5;
        Source[ 6] = 1; Dest[ 6] = 3; Weight[ 6] =  6;
        Source[ 7] = 3; Dest[ 7] = 4; Weight[ 7] =  7;
        Source[ 8] = 4; Dest[ 8] = 3; Weight[ 8] =  8;
        Source[ 9] = 4; Dest[ 9] = 0; Weight[ 9] =  9;
        Source[10] = 2; Dest[10] = 0; Weight[10] = 10;
        Source[11] = 2; Dest[11] = 0; Weight[11] = 11;
        Source[12] = 3; Dest[12] = 0; Weight[12] = 12;
        Source[13] = 0; Dest[13] = 2; Weight[13] = 13;
        Source[14] = 0; Dest[14] = 3; Weight[14] = 14;
        Source[15] = 3; Dest[15] = 0; Weight[15] = 15;
        Source[16] = 1; Dest[16] = 2; Weight[16] = 16;
        Source[17] = 2; Dest[17] = 1; Weight[17] = 17;
        Source[18] = 2; Dest[18] = 0; Weight[18] = 18;
        Source[19] = 3; Dest[19] = 4; Weight[19] = 19;
        */

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

