package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class LNEG extends gov.nasa.jpf.jvm.bytecode.LNEG {

	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo th) {

		StackFrame sf = th.getTopFrame();
		Abstraction abs_val = (Abstraction) sf.getOperandAttr(1);
		if (abs_val == null)
			return super.execute(ss, ks, th);
		else {
			long val = th.longPop(); // just to pop it

			Abstraction result = Abstraction._neg(abs_val);
			System.out.printf("LNEG> Values: %d (%s)\n", val, abs_val);

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
					System.out.printf("LNEG> Result: %s\n", result);
				}
			} else
				System.out.printf("LNEG> Result: %s\n", result);

			th.longPush(0);
			sf.setLongOperandAttr(result);

			return getNext(th);
		}
	}
	
}
