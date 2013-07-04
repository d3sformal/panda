package gov.nasa.jpf.abstraction.predicate;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.predicate.grammar.Predicates;
import gov.nasa.jpf.abstraction.predicate.state.FlatPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.Trace;

public class PredicateAbstraction extends Abstraction {
	private static List<PredicateAbstraction> instances = new LinkedList<PredicateAbstraction>();

	private ScopedSymbolTable symbolTable;
	private ScopedPredicateValuation predicateValuation;
	
	public PredicateAbstraction(Predicates predicateSet) {
		symbolTable = new ScopedSymbolTable();
		predicateValuation = new ScopedPredicateValuation(predicateSet);
		
		instances.add(this);
	}
	
	public static List<PredicateAbstraction> getInstances() {
		return instances;
	}
	
	public ScopedSymbolTable getSymbolTable() {
		return symbolTable;
	}
	
	public ScopedPredicateValuation getPredicateValuation() {
		return predicateValuation;
	}
	
	@Override
	public void start() {
		Trace trace = Trace.getInstance();
			
		FlatSymbolTable symbols = getSymbolTable().createDefaultScope();
		FlatPredicateValuation predicates = getPredicateValuation().createDefaultScope();
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
		
		getSymbolTable().store(trace.top().symbolTable);
		getPredicateValuation().store(trace.top().predicateValuation);
	}

	@Override
	public void forward() {
		System.err.println("Trace++");
		Trace trace = Trace.getInstance();
		
		FlatSymbolTable symbols = getSymbolTable().createDefaultScope();
		FlatPredicateValuation predicates = getPredicateValuation().createDefaultScope();
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
	}
	
	@Override
	public void backtrack() {
		System.err.println("Trace--");
		Trace trace = Trace.getInstance();
		
		trace.pop();
		
		getSymbolTable().restore(trace.top().symbolTable);
		getPredicateValuation().restore(trace.top().predicateValuation);
	}
}
