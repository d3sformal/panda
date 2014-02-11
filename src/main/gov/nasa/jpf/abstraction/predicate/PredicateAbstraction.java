package gov.nasa.jpf.abstraction.predicate;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.ObjectExpressionDecorator;
import gov.nasa.jpf.abstraction.common.impl.PrimitiveExpressionDecorator;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.BranchingCondition;
import gov.nasa.jpf.abstraction.common.BranchingConditionInfo;
import gov.nasa.jpf.abstraction.common.BranchingDecision;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.predicate.state.PredicateValuationStack;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.ScopedSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTableStack;
import gov.nasa.jpf.abstraction.predicate.state.Trace;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.predicate.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.ClassName;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.VM;

/**
 * Predicate abstraction class
 * 
 * Predicate abstraction is defined by a global
 * (in respect to individual runtime elements (objects))
 * container of all predicate valuations
 * 
 * Apart from that it uses an auxiliary symbol table -
 * a structure responsible for identification of aliases between
 * different access expressions (used in predicates or updated by instructions)
 */
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
		
		predicateValuation.reevaluate(to, affected, PrimitiveExpressionDecorator.wrap(from, symbolTable));
	}
	
	@Override
	public void processObjectStore(Expression from, AccessExpression to) {
		Set<AccessExpression> affected = symbolTable.processObjectStore(from, to);
				
		predicateValuation.reevaluate(to, affected, ObjectExpressionDecorator.wrap(from, symbolTable));
	}
	
	@Override
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		symbolTable.processMethodCall(threadInfo, before, after);
		
		predicateValuation.processMethodCall(threadInfo, before, after);
	}
	
	@Override
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		symbolTable.processMethodReturn(threadInfo, before, after);
		predicateValuation.processMethodReturn(threadInfo, before, after);
	}
	
	@Override
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		symbolTable.processVoidMethodReturn(threadInfo, before, after);
		predicateValuation.processVoidMethodReturn(threadInfo, before, after);
	}
	
	@Override
	public TruthValue processBranchingCondition(BranchingCondition condition) {
        Predicate predicate = (Predicate) condition;

		return predicateValuation.evaluatePredicate(predicate);
	}

    @Override
    public void processNewClass(ThreadInfo thread, ClassInfo classInfo) {
		// register class into the symbol table (universe)
        symbolTable.get(0).addClass(classInfo.getStaticElementInfo(), thread);
    }
	
	@Override
	public void informAboutBranchingDecision(BranchingDecision decision) {
        BranchingConditionValuation bcv = (BranchingConditionValuation) decision;

		predicateValuation.put(bcv.getCondition(), bcv.getValuation());
	}
	
	public ScopedSymbolTable getSymbolTable() {		
		return symbolTable;
	}
	
	public ScopedPredicateValuation getPredicateValuation() {	
		return predicateValuation;
	}
	
	@Override
	public void start(ThreadInfo mainThread) {
		SymbolTableStack symbols = new SymbolTableStack();
		PredicateValuationStack predicates = new PredicateValuationStack();

        // Initial state for last backtrack
		State state = new State(symbols, predicates);
		
		trace.push(state);

        /**
         * Register the main thread as it is not explicitely created elsewhere
         */
        symbolTable.get(0).addThread(mainThread);

        Set<ClassName> classes = new HashSet<ClassName>();

        // Make sure to add all objects of type java.lang.Class for startup builtin classes
        
        for (StructuredValueIdentifier value : symbolTable.getUniverse().getStructuredValues()) {
            if (value instanceof ClassName) {
                classes.add((ClassName) value);
            }
        }

        for (ClassName cls : classes) {
            symbolTable.get(0).addClassObject(cls, mainThread);
        }
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

        if (trace.isEmpty()) {
            predicateValuation.close();
        }
	}
}
