package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;

public class IntegerBinaryOperatorExecutor extends BinaryOperatorExecutor<Integer> {

	private static Object lock = new Object();
	private static IntegerBinaryOperatorExecutor instance;

	public static IntegerBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new IntegerBinaryOperatorExecutor();
				}
			}
		}
		
		return instance;
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
