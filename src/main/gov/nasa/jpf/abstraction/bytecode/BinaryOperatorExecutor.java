package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

public abstract class BinaryOperatorExecutor<T> {

	final public Instruction execute(AbstractBinaryOperator<T> op, ThreadInfo ti) {
		
		String name = op.getClass().getName();
		
		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();

		Abstraction abs_v1 = (Abstraction) sf.getOperandAttr(0);
		Abstraction abs_v2 = (Abstraction) sf.getOperandAttr(1);

		if (abs_v1 == null && abs_v2 == null) {
			return op.executeConcrete(ti);
		}

		T v1 = getLeft(sf);
		T v2 = getRight(sf);

		Abstraction result = op.getResult(v1, abs_v1, v2, abs_v2);

		System.out.printf("%s> Values: %f (%s), %f (%s)\n", name, v2, abs_v2, v1, abs_v1);

		if (result.isComposite()) {
			if (!ti.isFirstStepInsn()) { // first time around
				int size = result.getTokensNumber();
				ChoiceGenerator<?> cg = new FocusAbstractChoiceGenerator(size);
				ss.setNextChoiceGenerator(cg);

				return op.getSelf();
			} else { // this is what really returns results
				ChoiceGenerator<?> cg = ss.getChoiceGenerator();

				assert (cg instanceof FocusAbstractChoiceGenerator);

				int key = (Integer) cg.getNextChoice();
				result = result.getToken(key);
			}
		}
		
		System.out.printf("%s> Result: %s\n", name, result);

		cleanUp(sf);
		
		sf.setOperandAttr(result);

		return op.getNext(ti);
	}
	
	abstract protected T getLeft(StackFrame sf);
	abstract protected T getRight(StackFrame sf);
	abstract protected void cleanUp(StackFrame sf);
}
