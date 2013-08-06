package gov.nasa.jpf.abstraction.predicate;

import java.util.Set;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.PrimitiveExpression;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
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
	public void processLoad(ConcretePath from) {
		symbolTable.processLoad(from);
	}
	
	@Override
	public void processPrimitiveStore(Expression from, ConcretePath to) {		
		Set<AccessPath> affected = symbolTable.processPrimitiveStore(to);

		predicateValuation.reevaluate(to, affected, PrimitiveExpression.wrap(from));
	}
	
	@Override
	public void processObjectStore(Expression from, ConcretePath to) {	
		Set<AccessPath> affected = symbolTable.processObjectStore(from, to);
		
		predicateValuation.reevaluate(to, affected, ObjectExpression.wrap(from, symbolTable));
	}
	
	@Override
	public void processMethodCall(MethodInfo method) {
		symbolTable.processMethodCall(method);
		predicateValuation.processMethodCall(method);
	}
	
	@Override
	public void processMethodReturn(MethodInfo method) {
		symbolTable.processMethodReturn();
		predicateValuation.processMethodReturn();
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
