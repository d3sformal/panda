package gov.nasa.jpf.abstraction.predicate;

import java.util.Set;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.ObjectExpressionWrapper;
import gov.nasa.jpf.abstraction.common.impl.PrimitiveExpressionWrapper;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.state.PredicateValuationStack;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTableStack;
import gov.nasa.jpf.abstraction.predicate.state.Trace;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class PredicateAbstraction extends Abstraction {
	private ScopedSymbolTable symbolTable;
	private ScopedPredicateValuation predicateValuation;
	private Trace trace;
	
	public PredicateAbstraction(Predicates predicateSet) {
		symbolTable = new ScopedSymbolTable();
		predicateValuation = new ScopedPredicateValuation(predicateSet);
		trace = new Trace();
	}
	
	@Override
	public void processLoad(ConcreteAccessExpression from) {
		symbolTable.processLoad(from);
	}
	
	@Override
	public void processPrimitiveStore(Expression from, ConcreteAccessExpression to) {
		Set<AccessExpression> affected = symbolTable.processPrimitiveStore(to);
		
		predicateValuation.reevaluate(to, affected, PrimitiveExpressionWrapper.wrap(from, symbolTable));
	}
	
	@Override
	public void processObjectStore(Expression from, ConcreteAccessExpression to) {	
		Set<AccessExpression> affected = symbolTable.processObjectStore(from, to);
				
		predicateValuation.reevaluate(to, affected, ObjectExpressionWrapper.wrap(from, symbolTable));
	}
	
	@Override
	public void processMethodCall(ThreadInfo threadInfo, MethodInfo method) {
		symbolTable.processMethodCall(threadInfo, method);
		predicateValuation.processMethodCall(threadInfo, method);
	}
	
	@Override
	public void processMethodReturn(ThreadInfo threadInfo, MethodInfo method) {
		if (threadInfo != null && false) {
		
		} else {
			symbolTable.processMethodReturn();
			predicateValuation.processMethodReturn();
		}
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
