package gov.nasa.jpf.abstraction.state;

import java.util.Map;

/**
 * State of the predicate abstraction consists of
 * 1) history of scopes of symbol tables
 * 2) history of scopes of predicate valuations
 */
public class State {
    public int currentThread;
    public Map<Integer, SymbolTableStack> symbolTableStacks;
    public Map<Integer, PredicateValuationStack> predicateValuationStacks;

    public State(int currentThread, Map<Integer, SymbolTableStack> symbolTableStacks, Map<Integer, PredicateValuationStack> predicateValuationStacks) {
        this.currentThread = currentThread;
        this.symbolTableStacks = symbolTableStacks;
        this.predicateValuationStacks = predicateValuationStacks;
    }
}