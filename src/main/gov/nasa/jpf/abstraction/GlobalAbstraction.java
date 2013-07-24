package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.MethodInfo;

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
	public void start(MethodInfo method) {
		abs.start(method);
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
	public void processLoad(ConcretePath from) {
		if (!RunDetector.isRunning()) return;

		abs.processLoad(from);
	}
	
	@Override
	public void processPrimitiveStore(Expression from, ConcretePath to) {
		if (!RunDetector.isRunning()) return;
		
		abs.processPrimitiveStore(from, to);
	}
	
	@Override
	public void processObjectStore(ConcretePath from, ConcretePath to) {
		if (!RunDetector.isRunning()) return;
		
		abs.processObjectStore(from, to);
	}
	
	@Override
	public void processMethodCall(MethodInfo method) {
		abs.processMethodCall(method);
	}
	
	@Override
	public void processMethodReturn(MethodInfo method) {
		abs.processMethodReturn(method);
	}
	
	@Override
	public TruthValue evaluatePredicate(Predicate predicate) {
		if (!RunDetector.isRunning()) return TruthValue.UNDEFINED;
		
		return abs.evaluatePredicate(predicate);
	}
}
