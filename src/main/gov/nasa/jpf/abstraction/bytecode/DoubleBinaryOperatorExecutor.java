package gov.nasa.jpf.abstraction.bytecode;

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
