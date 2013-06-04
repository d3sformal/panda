package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.StackFrame;

public class FloatBinaryOperatorExecutor extends BinaryOperatorExecutor<Float> {

	private static FloatBinaryOperatorExecutor instance;

	public static FloatBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			instance = new FloatBinaryOperatorExecutor();
		}
		
		return instance;
	}
	
	@Override
	protected Abstraction getLeftAbstraction(StackFrame sf) {
		return (Abstraction)sf.getOperandAttr(0);
	}

	@Override
	protected Abstraction getRightAbstraction(StackFrame sf) {
		return (Abstraction)sf.getOperandAttr(1);
	}
	
	@Override
	final protected Float getLeft(StackFrame sf) {
		return sf.peekFloat(0);
	}

	@Override
	final protected Float getRight(StackFrame sf) {
		return sf.peekFloat(1);
	}

	@Override
	final protected void cleanUp(StackFrame sf) {
		sf.popFloat();
		sf.popFloat();
		
		sf.pushFloat(0);
	}

}
