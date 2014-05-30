package gov.nasa.jpf.abstraction.predicate.state;


import java.util.Stack;

/**
 * Overall history of states visited on a particular path.
 */
public class Trace {
    private Stack<State> states = new Stack<State>();

    public State top() {
        return top(0);
    }

    public State top(int i) {
        return states.get(states.size() - i - 1);
    }

    public State get(int i) {
        return states.get(i);
    }

    public void pop() {
        states.pop();
    }

    public void push(State state) {
        states.push(state);
    }

    public int size() {
        return states.size();
    }

    public boolean isEmpty() {
        return states.isEmpty();
    }
}
