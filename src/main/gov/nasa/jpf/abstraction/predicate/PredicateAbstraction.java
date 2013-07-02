package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.predicate.grammar.Context;
import gov.nasa.jpf.abstraction.predicate.grammar.Predicate;
import gov.nasa.jpf.abstraction.predicate.grammar.Predicates;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.Trace;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public class PredicateAbstraction extends Abstraction {
	private Predicates predicateSet;
	
	public PredicateAbstraction(Predicates predicateSet) {
		this.predicateSet = predicateSet;
	}

	@Override
	public void forward() {
		System.err.println("Trace++");
		Trace trace = Trace.getInstance();

		//TODO: DEBUG:
		for (Context context : predicateSet.contexts) {
			for (Predicate predicate : context.predicates) {
				ScopedPredicateValuation.getInstance().put(predicate, TruthValue.UNDEFINED);
			}
		}
		
		State state = new State(ScopedSymbolTable.getInstance().memorize(), ScopedPredicateValuation.getInstance().memorize());
		
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
