package gov.nasa.jpf.abstraction.predicate.state;

public class State {
	public SymbolTableStack symbolTableStack;
	public PredicateValuationStack predicateValuationStack;
	
	public State(SymbolTableStack symbolTableStack, PredicateValuationStack predicateValuationStack) {
		this.symbolTableStack = symbolTableStack;
		this.predicateValuationStack = predicateValuationStack;
	}
}
