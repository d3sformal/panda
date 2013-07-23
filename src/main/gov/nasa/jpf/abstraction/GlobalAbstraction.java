package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.MethodInfo;

public class GlobalAbstraction extends Abstraction {
	private static GlobalAbstraction instance;
	
	public static void set(String targetClass, Abstraction abs) {
		instance = new GlobalAbstraction(targetClass, abs);
	}
	
	public static GlobalAbstraction getInstance() {
		return instance;
	}
	
	private String targetClass;
	private Abstraction abs;
	private boolean running;
	
	private GlobalAbstraction(String targetClass, Abstraction abs) {
		this.targetClass = targetClass;
		this.abs = abs;
		this.running = false;
		
		System.err.println(targetClass);
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
	public void start(MethodInfo method) {
		abs.start(method);
	}
	
	@Override
	public void forward(MethodInfo method) {
		abs.forward(method);
	}
	
	@Override
	public void backtrack() {
		abs.backtrack();
	}
	
	@Override
	public void processLoad(ConcretePath from) {
		if (!running) return;

		abs.processLoad(from);
	}
	
	@Override
	public void processStore(Expression from, ConcretePath to) {
		if (!running) return;
		
		abs.processStore(from, to);
	}
	
	@Override
	public void processMethodCall(MethodInfo method) {
		if (method.getClassName().equals(targetClass) && method.getName().equals("main")) {
			running = true;
		}
		
		abs.processMethodCall(method);
	}
	
	@Override
	public void processMethodReturn(MethodInfo method) {
		if (method.getClassName().equals(targetClass) && method.getName().equals("main")) {
			running = false;
		}

		abs.processMethodReturn(method);
	}
	
	@Override
	public TruthValue evaluatePredicate(Predicate predicate) {
		if (!running) return TruthValue.FALSE;
		
		return abs.evaluatePredicate(predicate);
	}
}
