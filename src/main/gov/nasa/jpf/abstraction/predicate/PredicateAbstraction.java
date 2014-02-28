package gov.nasa.jpf.abstraction.predicate;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
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
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.predicate.state.PredicateValuationStack;
import gov.nasa.jpf.abstraction.predicate.state.SystemPredicateValuation;
import gov.nasa.jpf.abstraction.predicate.state.SystemSymbolTable;
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
	private SystemSymbolTable symbolTable;
	private SystemPredicateValuation predicateValuation;
	private Trace trace;
    private boolean isInitialized = false;
    private Set<ClassInfo> startupClasses = new HashSet<ClassInfo>();

	public PredicateAbstraction(Predicates predicateSet) {
		symbolTable = new SystemSymbolTable(this);
		predicateValuation = new SystemPredicateValuation(this, predicateSet);
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

    public Integer computePreciseExpressionValue(Expression expression) {
        return predicateValuation.evaluateExpression(expression);
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
        if (isInitialized) {
            symbolTable.get(0).addClass(classInfo.getStaticElementInfo(), thread);
        } else {
            startupClasses.add(classInfo);
        }
    }
	
    @Override
    public void informAboutPrimitiveLocalVariable(Root root) {
        symbolTable.get(0).ensurePrimitiveLocalVariableExistence(root);
    }
    
    @Override
    public void informAboutStructuredLocalVariable(Root root) {
        symbolTable.get(0).ensureStructuredLocalVariableExistence(root);
    }
    
	@Override
	public void informAboutBranchingDecision(BranchingDecision decision) {
        BranchingConditionValuation bcv = (BranchingConditionValuation) decision;

		predicateValuation.put(bcv.getCondition(), bcv.getValuation());
	}

    @Override
    public void addThread(ThreadInfo threadInfo) {
        symbolTable.addThread(threadInfo);
        predicateValuation.addThread(threadInfo);
    }

    @Override
    public void scheduleThread(ThreadInfo threadInfo) {
        symbolTable.scheduleThread(threadInfo);
        predicateValuation.scheduleThread(threadInfo);
    }
	
	public SystemSymbolTable getSymbolTable() {		
		return symbolTable;
	}
	
	public SystemPredicateValuation getPredicateValuation() {	
		return predicateValuation;
	}
	
	@Override
	public void start(ThreadInfo mainThread) {
		Map<Integer, SymbolTableStack> symbols = new HashMap<Integer, SymbolTableStack>();
		Map<Integer, PredicateValuationStack> predicates = new HashMap<Integer, PredicateValuationStack>();

        // Initial state for last backtrack
		State state = new State(mainThread.getId(), symbols, predicates);
		
		trace.push(state);

        /**
         * Register the main thread as it is not explicitely created elsewhere
         */
        addThread(mainThread);
        scheduleThread(mainThread);

        isInitialized = true;

        for (ClassInfo cls : startupClasses) {
            processNewClass(mainThread, cls);
        }
	}

	@Override
	public void forward(MethodInfo method) {		
		State state = new State(VM.getVM().getCurrentThread().getId(), symbolTable.memorize(), predicateValuation.memorize());
		
		trace.push(state);
	}
	
	@Override
	public void backtrack(MethodInfo method) {		
		trace.pop();
		
		symbolTable.restore(trace.top().symbolTableStacks);
        symbolTable.scheduleThread(trace.top().currentThread);
		predicateValuation.restore(trace.top().predicateValuationStacks);
        predicateValuation.scheduleThread(trace.top().currentThread);

        if (trace.isEmpty()) {
            predicateValuation.close();
        }
	}

    public Trace getTrace() {
        return trace;
    }

    public void collectGarbage(VM vm, ThreadInfo threadInfo) {
        if (isInitialized) {
            symbolTable.collectGarbage(vm, threadInfo);
        }
    }
}
