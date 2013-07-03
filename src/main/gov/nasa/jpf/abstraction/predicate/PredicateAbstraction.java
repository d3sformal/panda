package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.predicate.grammar.Predicates;
import gov.nasa.jpf.abstraction.predicate.state.FlatPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.Trace;

public class PredicateAbstraction extends Abstraction {
	private Predicates predicateSet;
	
	public PredicateAbstraction(Predicates predicateSet) {
		this.predicateSet = predicateSet;
	}
	
	@Override
	public void start() {
		Trace trace = Trace.getInstance();
		
		ScopedPredicateValuation.getInstance().setPredicateSet(predicateSet);
		
		FlatSymbolTable symbols = ScopedSymbolTable.getInstance().createDefaultSymbolTable();
		FlatPredicateValuation predicates = ScopedPredicateValuation.getInstance().createDefaultPredicateValuation();
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
		
		ScopedSymbolTable.getInstance().store(trace.top().symbolTable);
		ScopedPredicateValuation.getInstance().store(trace.top().predicateValuation);
	}

	@Override
	public void forward() {
		System.err.println("Trace++");
		Trace trace = Trace.getInstance();
		
		FlatSymbolTable symbols = ScopedSymbolTable.getInstance().createDefaultSymbolTable();
		FlatPredicateValuation predicates = ScopedPredicateValuation.getInstance().createDefaultPredicateValuation();
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
		
		//TODO: DEBUG:
		System.err.println(state.symbolTable.toString());
		System.err.println(state.predicateValuation.toString());
	}
	
	@Override
	public void backtrack() {
		System.err.println("Trace--");
		Trace trace = Trace.getInstance();
		
		trace.pop();
		
		ScopedSymbolTable.getInstance().restore(trace.top().symbolTable);
		ScopedPredicateValuation.getInstance().restore(trace.top().predicateValuation);
	}
}
