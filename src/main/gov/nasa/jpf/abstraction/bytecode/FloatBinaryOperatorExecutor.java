package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.Types;

public class FloatBinaryOperatorExecutor extends BinaryOperatorExecutor<Float> {

	private static FloatBinaryOperatorExecutor instance;

	public static FloatBinaryOperatorExecutor getInstance() {
		if (instance == null) {
			instance = new FloatBinaryOperatorExecutor();
		}
		
		return instance;
	}
	
	@Override
	final protected Float getLeft(StackFrame sf) {
		return Types.intToFloat(sf.peek(0));
	}

	@Override
	final protected Float getRight(StackFrame sf) {
		return Types.intToFloat(sf.peek(1));
	}

	@Override
	final protected void cleanUp(StackFrame sf) {
		sf.pop();
		sf.pop();
		
		sf.push(0, false);
	}

}
