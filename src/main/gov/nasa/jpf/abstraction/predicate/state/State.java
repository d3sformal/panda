package gov.nasa.jpf.abstraction.predicate.state;

import java.util.Map;

/**
 * State of the predicate abstraction consists of
 * 1) history of scopes of symbol tables
 * 2) history of scopes of predicate valuations
 */
public class State {
	public Map<Integer, SymbolTableStack> symbolTableStacks;
	public Map<Integer, PredicateValuationStack> predicateValuationStacks;
	
	public State(Map<Integer, SymbolTableStack> symbolTableStacks, Map<Integer, PredicateValuationStack> predicateValuationStacks) {
		this.symbolTableStacks = symbolTableStacks;
		this.predicateValuationStacks = predicateValuationStacks;
	}
}
