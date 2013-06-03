package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.Types;

public class DoubleBinaryOperatorExecutor extends BinaryOperatorExecutor<Double> {

	private static DoubleBinaryOperatorExecutor instance;

	public static DoubleBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			instance = new DoubleBinaryOperatorExecutor();
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
	final protected Double getLeft(StackFrame sf) {
		return Types.longToDouble(sf.peekLong(0));
	}

	@Override
	final protected Double getRight(StackFrame sf) {
		return Types.longToDouble(sf.peekLong(2));
	}

	@Override
	final protected void cleanUp(StackFrame sf) {
		sf.popLong();
		sf.popLong();
		
		sf.pushLong(0);
	}

}
