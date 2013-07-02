package gov.nasa.jpf.abstraction.predicate.state;


public class State {
	public FlatSymbolTable symbolTable;
	public FlatPredicateValuation predicateValuation;
	
	public State(FlatSymbolTable symbolTable, FlatPredicateValuation predicateValuation) {
		this.symbolTable = symbolTable;
		this.predicateValuation = predicateValuation;
	}
}
