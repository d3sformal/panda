package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.StackFrame;

public class IntegerBinaryOperatorExecutor extends BinaryOperatorExecutor<Integer> {

	private static IntegerBinaryOperatorExecutor instance;

	public static IntegerBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			instance = new IntegerBinaryOperatorExecutor();
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
	protected Integer getLeft(StackFrame sf) {
		return sf.peek(0);
	}

	@Override
	protected Integer getRight(StackFrame sf) {
		return sf.peek(1);
	}

	@Override
	protected void cleanUp(StackFrame sf) {
		sf.pop();
		sf.pop();
		
		sf.push(0, false);
	}

}
