package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.numeric.AbstractBoolean;
import gov.nasa.jpf.abstraction.numeric.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class IFNE extends gov.nasa.jpf.jvm.bytecode.IFNE {

	public IFNE(int targetPc) {
		super(targetPc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Instruction execute(SystemState ss, KernelState ks, ThreadInfo ti) {

		StackFrame sf = ti.getTopFrame();
		Abstraction abs_v = (Abstraction) sf.getOperandAttr();

		if (abs_v == null) { // the condition is concrete
			return super.execute(ss, ks, ti);
		} else { // the condition is abstract

			System.out.printf("IFNE> Values: %d (%s)\n", ti.peek(0), abs_v);
			AbstractBoolean abs_condition = abs_v._ne(0);

			if (abs_condition == AbstractBoolean.TRUE)
				conditionValue = true;
			else if (abs_condition == AbstractBoolean.FALSE)
				conditionValue = false;
			else { // TOP
				ChoiceGenerator<?> cg;
				if (!ti.isFirstStepInsn()) { // first time around
					cg = new AbstractChoiceGenerator();
					ss.setNextChoiceGenerator(cg);
					return this;
				} else { // this is what really returns results
					cg = ss.getChoiceGenerator();
					assert (cg instanceof AbstractChoiceGenerator) : "expected AbstractChoiceGenerator, got: "
							+ cg;
					conditionValue = (Integer) cg.getNextChoice() == 0 ? false
							: true;
				}
			}

			System.out.println("IFNE> Result: " + conditionValue);
			ti.pop();
			return (conditionValue ? getTarget() : getNext(ti));

		}
	}

}
