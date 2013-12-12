package gov.nasa.jpf.abstraction.predicate.state;


import java.util.Stack;

/**
 * Overall history of states visited on a particular path.
 */
public class Trace {
	private Stack<State> states = new Stack<State>();
	
	public State top() {
		return states.lastElement();
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
