package gov.nasa.jpf.abstraction.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.StaticSingleAssignmentFormulaFormatter;
import gov.nasa.jpf.abstraction.TraceFormula;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.util.Pair;

/**
 * State of the predicate abstraction consists of
 * 1) history of scopes of symbol tables
 * 2) history of scopes of predicate valuations
 */
public class State {
    public int currentThread;
    public Map<Integer, SymbolTableStack> symbolTableStacks;
    public Map<Integer, PredicateValuationStack> predicateValuationStacks;
    public TraceFormula traceFormula;
    public StaticSingleAssignmentFormulaFormatter ssa;

    public State(
        int currentThread,
        Map<Integer, SymbolTableStack> symbolTableStacks,
        Map<Integer, PredicateValuationStack> predicateValuationStacks,
        TraceFormula traceFormula,
        StaticSingleAssignmentFormulaFormatter ssa
    ) {
        this.currentThread = currentThread;
        this.symbolTableStacks = symbolTableStacks;
        this.predicateValuationStacks = predicateValuationStacks;
        this.traceFormula = traceFormula;
        this.ssa = ssa;
    }
}
