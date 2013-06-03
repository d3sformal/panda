package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.StackFrame;

public class LongBinaryOperatorExecutor extends BinaryOperatorExecutor<Long> {

	private static LongBinaryOperatorExecutor instance;

	public static LongBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			instance = new LongBinaryOperatorExecutor();
		}
		
		return instance;
	}
	
	@Override
	protected Abstraction getLeftAbstraction(StackFrame sf) {
		return (Abstraction)sf.getOperandAttr(1);
	}

	@Override
	protected Abstraction getRightAbstraction(StackFrame sf) {
		return (Abstraction)sf.getOperandAttr(3);
	}

	@Override
	protected Long getLeft(StackFrame sf) {
		return sf.peekLong(0);
	}

	@Override
	protected Long getRight(StackFrame sf) {
		return sf.peekLong(2);
	}

	@Override
	protected void cleanUp(StackFrame sf) {
		sf.popLong();
		sf.popLong();
		
		sf.pushLong(0);
	}

}
