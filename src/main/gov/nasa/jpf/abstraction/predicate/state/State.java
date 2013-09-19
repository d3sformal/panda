package gov.nasa.jpf.abstraction.predicate.state;

/**
 * State of the predicate abstraction consists of
 * 1) history of scopes of symbol tables
 * 2) history of scopes of predicate valuations
 */
public class State {
	public SymbolTableStack symbolTableStack;
	public PredicateValuationStack predicateValuationStack;
	
	public State(SymbolTableStack symbolTableStack, PredicateValuationStack predicateValuationStack) {
		this.symbolTableStack = symbolTableStack;
		this.predicateValuationStack = predicateValuationStack;
	}
}
