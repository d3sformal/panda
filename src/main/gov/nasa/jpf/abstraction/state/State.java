package gov.nasa.jpf.abstraction.state;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.StaticSingleAssignmentFormulaFormatter;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

/**
 * State of the predicate abstraction consists of
 * 1) history of scopes of symbol tables
 * 2) history of scopes of predicate valuations
 */
public class State {
    public int currentThread;
    public Map<Integer, SymbolTableStack> symbolTableStacks;
    public Map<Integer, PredicateValuationStack> predicateValuationStacks;
    public Predicate traceFormula;
    public StaticSingleAssignmentFormulaFormatter ssa;

    public State(int currentThread, Map<Integer, SymbolTableStack> symbolTableStacks, Map<Integer, PredicateValuationStack> predicateValuationStacks, Predicate traceFormula, StaticSingleAssignmentFormulaFormatter ssa) {
        this.currentThread = currentThread;
        this.symbolTableStacks = symbolTableStacks;
        this.predicateValuationStacks = predicateValuationStacks;
        this.traceFormula = traceFormula;
        this.ssa = ssa;
    }
}
