package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.predicate.grammar.Predicates;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.Trace;

public class PredicateAbstraction extends Abstraction {
	private Predicates predicateSet;
	
	public PredicateAbstraction(Predicates predicateSet) {
		this.predicateSet = predicateSet;
	}
	
	@Override
	public void forward() {
		System.err.println("Trace++");
		Trace trace = Trace.getInstance();
		
		State state = new State(ScopedSymbolTable.getInstance().memorize());
		
		trace.push(state);
		
		//TODO: DEBUG:
		System.err.println(state.symbolTable.toString());
	}
	
	@Override
	public void backtrack() {
		System.err.println("Trace--");
		Trace trace = Trace.getInstance();
		
		trace.pop();
		
		ScopedSymbolTable.getInstance().restore(trace.top().symbolTable);
	}
}
