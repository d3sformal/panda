package svcomp.loops;

public class ArrayTrueUnreachableLabel {
    public static void main(String[] args) {
        int SIZE = 1;
        int[] array = new int[SIZE];
        int menor = unknown();

        for (int j = 0; j < SIZE; ++j) {
            array[j] = unknown();

            if (array[j] <= menor) {
                menor = array[j];
            }
        }

        assert array[0] >= menor;
    }

    public static int unknown() {
        return 0;
    }
}
