package branch;

public class Branch {
    public static void main(String[] args) {
        boolean b;
        b = true;
        int i;

        // 1st Choice
        if (b) {
            i = 1;
        } else {
            i = 2;
        }

        // 2nd Choice
        if (b) {
            i = 3;
        } else {
            i = 4;
        }

        // End
        System.out.println(i);
    }
}
