package gov.nasa.jpf.abstraction.predicate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.state.FlatPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.Trace;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;

public class PredicateAbstraction extends Abstraction {
	private static List<PredicateAbstraction> instances = new LinkedList<PredicateAbstraction>();

	// SYMBOLS DO NOT DEPEND ON ABSTRACTION AND DO NOT NEED TO BE MANAGED SEPARATELY
	// FOR ALL INSTANCES
	private static ScopedSymbolTable symbolTable = new ScopedSymbolTable();
	private ScopedPredicateValuation predicateValuation;
	private Trace trace;
	
	public PredicateAbstraction(Predicates predicateSet) {
		instances.add(this);

		predicateValuation = new ScopedPredicateValuation(predicateSet);
		trace = new Trace();
	}
	
	public static void processLoad(Map<AccessPath, CompleteVariableID> vars) {
		for (AccessPath path : vars.keySet()) {
			symbolTable.processLoad(path, vars.get(path));
		}
	}
	
	public static void processStore(ConcretePath from, ConcretePath to, StackFrame sf) {
		Set<AccessPath> affected = symbolTable.processStore(from, to);

		for (PredicateAbstraction abs : instances) {
			abs.predicateValuation.reevaluate(affected);
		}
		
		System.err.println("===== Stack expressions =====");
		for (int i = 0; i < sf.getLocalVariableCount(); ++i) {
			Attribute attr = (Attribute) sf.getLocalAttr(i);
			
			if (attr == null) attr = new EmptyAttribute();
			
			LocalVarInfo var = sf.getLocalVarInfo(i);
			
			String name = var == null ? null : var.getName();

			System.err.println("\t" + name  + ": " + attr.getExpression());
		}
		System.err.println("\n");
	}
	
	public static void processMethodCall() {
		symbolTable.processMethodCall();

		for (PredicateAbstraction abs : instances) {
			abs.predicateValuation.processMethodCall();
		}
	}
	
	public static void processMethodReturn() {
		symbolTable.processMethodReturn();

		for (PredicateAbstraction abs : instances) {
			abs.predicateValuation.processMethodReturn();
		}
	}
	
	public static List<ScopedSymbolTable> getSymbolTables() {
		List<ScopedSymbolTable> symbolTables = new LinkedList<ScopedSymbolTable>();
		
		symbolTables.add(symbolTable);
		
		return symbolTables;
	}
	
	public static List<ScopedPredicateValuation> getPredicateValuations() {
		List<ScopedPredicateValuation> predicateValuations = new LinkedList<ScopedPredicateValuation>();
		
		for (PredicateAbstraction abs : instances) {
			predicateValuations.add(abs.predicateValuation);
		}
		
		return predicateValuations;
	}
	
	@Override
	public void start() {	
		FlatSymbolTable symbols = symbolTable.createDefaultScope();
		FlatPredicateValuation predicates = predicateValuation.createDefaultScope();
		
		State state = new State(symbols, predicates);
		
		trace.push(state);
		
		symbolTable.store(trace.top().symbolTable);
		predicateValuation.store(trace.top().predicateValuation);
	}

	@Override
	public void forward() {
		System.err.println("Trace++");
		
		FlatSymbolTable symbols = symbolTable.createDefaultScope();
		FlatPredicateValuation predicates = predicateValuation.createDefaultScope();
		
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
