package gov.nasa.jpf.abstraction.predicate.common;

import java.util.Stack;

public class Trace {
	private static Trace instance;
	
	private Stack<State> states = new Stack<State>();
	
	private Trace() {
		states.push(new State(new FlatSymbolTable()));
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
}
