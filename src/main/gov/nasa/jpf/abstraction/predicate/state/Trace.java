package gov.nasa.jpf.abstraction.predicate.state;


import java.util.Stack;

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
}
