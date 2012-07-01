package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class FNEG extends gov.nasa.jpf.jvm.bytecode.FNEG {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr(0);
		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			float val = Types.intToFloat(th.pop()); // just to pop it

			Abstraction result = Abstraction._neg(abs_val);
			System.out.printf("Values: %f (%s)\n", val, abs_val);
			if (result.isTop()) {
				ChoiceGenerator<?> cg;
				if (!th.isFirstStepInsn()) { // first time around
					int size = result.get_num_tokens();
					cg = new FocusAbstractChoiceGenerator(size);
					ss.setNextChoiceGenerator(cg);
					return this;
				} else { // this is what really returns results
					cg = ss.getChoiceGenerator();
					assert (cg instanceof FocusAbstractChoiceGenerator);
					int key = (Integer) cg.getNextChoice();
					result = result.get_token(key);
					System.out.printf("Result: %s\n", result);
				}
			} else
				System.out.printf("Result: %s\n", result);

			th.push(0, false);
			sf.setOperandAttr(result);

			System.out.println("Execute FNEG: " + result);

			return getNext(th);
		}
	}	
	
}
