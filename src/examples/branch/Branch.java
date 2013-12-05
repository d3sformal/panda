package branch;

public class Branch {
    public static void changeHeap() {
        new Branch();
    }

    public static void main(String[] args) {
        boolean b = true, c = true;

        while (b) {
            //changeHeap(); // Cannot change heap incrementally like this ... states would not match ... infinite loop
        };

// Learnt predicates distinguish states ... no matching at the end of ifs (would need to merge states, detect whether Force(else-branch, condition = true) = true-branch, if so remove then-branch state, store merged state :)
/*
        if (c) {
            changeHeap();
        } else {
        }

        if (c) {
            changeHeap();
        } else {
        }

        // Reachable
        // a) Empty heap ... else else
        // b) 1 object   ... then else | else then
        // c) 2 objects  ... then then

        if (c) { // Breaks transition
        } else {
        }
*/
    }
}
