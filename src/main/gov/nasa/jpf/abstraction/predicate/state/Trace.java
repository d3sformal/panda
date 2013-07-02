package gov.nasa.jpf.abstraction.predicate.state;


import java.util.Stack;

public class Trace {
	private static Trace instance;
	
	private Stack<State> states = new Stack<State>();
	
	private Trace() {
		states.push(new State(new FlatSymbolTable(), new FlatPredicateValuation()));
	}
	
	public static Trace getInstance() {
		if (instance == null) {
			instance = new Trace();
		}
		
		return instance;
	}
	
	public State top() {
		return states.lastElement();
	}
	
	public void pop() {
		if (states.size() > 1) {
			states.pop();
		}
	}
	
	public void push(State state) {
		states.push(state);
	}
}
