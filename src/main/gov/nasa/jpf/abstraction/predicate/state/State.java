package gov.nasa.jpf.abstraction.predicate.state;


public class State {
	public FlatSymbolTable symbolTable;
	
	public State(FlatSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
}
