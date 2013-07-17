package gov.nasa.jpf.abstraction.predicate;

import java.util.Map;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.state.FlatPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.Trace;
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
	public void processLoad(Map<AccessPath, CompleteVariableID> vars) {
		for (AccessPath path : vars.keySet()) {
			symbolTable.processLoad(path, vars.get(path));
		}
	}
	
	@Override
	public void processStore(Expression from, ConcretePath to) {
		ConcretePath fromPath = null;
		
		if (from instanceof ConcretePath) {
			fromPath = (ConcretePath) from;
		}
		
		symbolTable.processStore(fromPath, to);

		predicateValuation.reevaluate(to, from);
	}
	
	@Override
	public void processMethodCall(MethodInfo method) {
		symbolTable.processMethodCall(method);
		predicateValuation.processMethodCall(method);
	}
	
	@Override
	public void processMethodReturn() {
		symbolTable.processMethodReturn();
		predicateValuation.processMethodReturn();
	}
	
	public ScopedSymbolTable getSymbolTable() {		
		return symbolTable;
	}
	
	public ScopedPredicateValuation getPredicateValuation() {	
		return predicateValuation;
	}
	
	@Override
	public void start(MethodInfo method) {	
		FlatSymbolTable symbols = symbolTable.createDefaultScope(method);
		FlatPredicateValuation predicates = predicateValuation.createDefaultScope(method);
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
		
		symbolTable.store(trace.top().symbolTable);
		predicateValuation.store(trace.top().predicateValuation);
	}

	@Override
	public void forward(MethodInfo method) {
		System.err.println("Trace++");
		
		FlatSymbolTable symbols = symbolTable.createDefaultScope(method);
		FlatPredicateValuation predicates = predicateValuation.createDefaultScope(method);
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
	}
	
	@Override
	public void backtrack() {
		System.err.println("Trace--");
		
		trace.pop();
		
		symbolTable.restore(trace.top().symbolTable);
		predicateValuation.restore(trace.top().predicateValuation);
	}
}
