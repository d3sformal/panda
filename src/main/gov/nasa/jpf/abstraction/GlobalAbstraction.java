package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.BranchingCondition;
import gov.nasa.jpf.abstraction.common.BranchingConditionInfo;
import gov.nasa.jpf.abstraction.common.BranchingDecision;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * GlobalAbstraction wraps all the abstractions (any arbitrary / container abstraction) instantiated by our InstructionFactory
 * It uses RunDetector to decide what notifications should be passed to the singleton instance
 */
public class GlobalAbstraction extends Abstraction {
	private static GlobalAbstraction instance;
	
	public static void set(Abstraction abs) {
		instance = new GlobalAbstraction(abs);
	}
	
	public static GlobalAbstraction getInstance() {
		return instance;
	}
	
	private Abstraction abs;
	
	private GlobalAbstraction(Abstraction abs) {
		this.abs = abs;
	}
	
	public Abstraction get() {
		return abs;
	}
	
	@Override
	public int getDomainSize() {
		return abs.getDomainSize();
	}
	
	@Override
	public AbstractValue abstractMap(int value) {
		return abs.abstractMap(value);
	}
	
	@Override
	public AbstractValue abstractMap(float value) {
		return abs.abstractMap(value);
	}
	
	@Override
	public AbstractValue abstractMap(long value) {
		return abs.abstractMap(value);
	}
	
	@Override
	public AbstractValue abstractMap(double value) {
		return abs.abstractMap(value);
	}
	
	@Override
	public void start(ThreadInfo threadInfo) {
		abs.start(threadInfo);
	}
	
	@Override
	public void forward(MethodInfo method) {
		abs.forward(method);
	}
	
	@Override
	public void backtrack(MethodInfo method) {
		abs.backtrack(method);
	}
	
	@Override
	public void processPrimitiveStore(Expression from, AccessExpression to) {
		//if (!RunDetector.isRunning()) return; // Cannot be omitted because we need MethodFrameSymbolTable / Universe to be updated properly (for example AALOAD needs lookupValues)
		
		abs.processPrimitiveStore(from, to);
	}
	
	@Override
	public void processObjectStore(Expression from, AccessExpression to) {
		//if (!RunDetector.isRunning()) return; // Cannot be omitted because we need MethodFrameSymbolTable / Universe to be updated properly (for example AALOAD needs lookupValues)
		
		abs.processObjectStore(from, to);
	}
	
	@Override
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		abs.processMethodCall(threadInfo, before, after);
	}
	
	@Override
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {		
		abs.processMethodReturn(threadInfo, before, after);
	}
	
	@Override
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {		
		abs.processVoidMethodReturn(threadInfo, before, after);
	}
	
	@Override
	public BranchingConditionInfo processBranchingCondition(BranchingCondition condition) {
		if (!RunDetector.isRunning()) return BranchingConditionInfo.NONE;
		
		return abs.processBranchingCondition(condition);
	}

    @Override
    public void processNewClass(ThreadInfo thread, ClassInfo classInfo) {
        abs.processNewClass(thread, classInfo);
    }

    @Override
    public void processNewObject(AnonymousObject object) {
        abs.processNewObject(object);
    }

    @Override
    public void informAboutPrimitiveLocalVariable(Root root) {
        abs.informAboutPrimitiveLocalVariable(root);
    }

    @Override
    public void informAboutStructuredLocalVariable(Root root) {
        abs.informAboutStructuredLocalVariable(root);
    }
	
	@Override
	public void informAboutBranchingDecision(BranchingDecision decision) {
		if (!RunDetector.isRunning()) return;
		
		abs.informAboutBranchingDecision(decision);
	}

    @Override
    public void addThread(ThreadInfo threadInfo) {
        abs.addThread(threadInfo);
    }

    @Override
    public void scheduleThread(ThreadInfo threadInfo) {
        abs.scheduleThread(threadInfo);
    }
}
