package gov.nasa.jpf.abstraction.predicate;

import java.util.Set;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.ObjectExpressionWrapper;
import gov.nasa.jpf.abstraction.common.impl.PrimitiveExpressionWrapper;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.state.AffectedAccessExpressions;
import gov.nasa.jpf.abstraction.predicate.state.PredicateValuationStack;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTableStack;
import gov.nasa.jpf.abstraction.predicate.state.Trace;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class PredicateAbstraction extends Abstraction {
	private ScopedSymbolTable symbolTable;
	private ScopedPredicateValuation predicateValuation;
	private Trace trace;
	
	public PredicateAbstraction(Predicates predicateSet) {
		symbolTable = new ScopedSymbolTable(this);
		predicateValuation = new ScopedPredicateValuation(this, predicateSet);
		trace = new Trace();
	}
	
	@Override
	public void processPrimitiveStore(Expression from, AccessExpression to) {
		Set<AccessExpression> affected = symbolTable.processPrimitiveStore(from, to);
		
		predicateValuation.reevaluate(to, affected, PrimitiveExpressionWrapper.wrap(from, symbolTable));
	}
	
	@Override
	public void processObjectStore(AccessExpression from, AccessExpression to) {	
		Set<AccessExpression> affected = symbolTable.processObjectStore(from, to);
				
		predicateValuation.reevaluate(to, affected, ObjectExpressionWrapper.wrap(from, symbolTable));
	}
	
	@Override
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		AffectedAccessExpressions affected = symbolTable.processMethodCall(threadInfo, before, after, null);
		
		predicateValuation.processMethodCall(threadInfo, before, after, affected);
	}
	
	@Override
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		AffectedAccessExpressions affected = symbolTable.processMethodReturn(threadInfo, before, after, null);
		predicateValuation.processMethodReturn(threadInfo, before, after, affected);
	}
	
	@Override
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		AffectedAccessExpressions affected = symbolTable.processVoidMethodReturn(threadInfo, before, after, null);
		predicateValuation.processVoidMethodReturn(threadInfo, before, after, affected);
	}
	
	@Override
	public TruthValue evaluatePredicate(Predicate predicate) {
		return predicateValuation.evaluatePredicate(predicate);
	}
	
	@Override
	public void forceValuation(Predicate predicate, TruthValue valuation) {
		predicateValuation.put(predicate, valuation);
	}
	
	public ScopedSymbolTable getSymbolTable() {		
		return symbolTable;
	}
	
	public ScopedPredicateValuation getPredicateValuation() {	
		return predicateValuation;
	}
	
	@Override
	public void start(MethodInfo method) {	
		SymbolTableStack symbols = new SymbolTableStack();
		PredicateValuationStack predicates = new PredicateValuationStack();
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
	}

	@Override
	public void forward(MethodInfo method) {		
		State state = new State(symbolTable.memorize(), predicateValuation.memorize());
		
		trace.push(state);
	}
	
	@Override
	public void backtrack(MethodInfo method) {		
		trace.pop();
		
		symbolTable.restore(trace.top().symbolTableStack);
		predicateValuation.restore(trace.top().predicateValuationStack);
	}
}
