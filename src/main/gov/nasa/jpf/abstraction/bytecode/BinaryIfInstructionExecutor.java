package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

public class BinaryIfInstructionExecutor {

	final public Instruction execute(AbstractBranching br, ThreadInfo ti) {
		
		String name = br.getClass().getName();

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		AbstractValue abs_v1 = getLeftAbstractValue(sf);
		AbstractValue abs_v2 = getRightAbstractValue(sf);
		
		boolean conditionValue;

		if (abs_v1 == null && abs_v2 == null) { // the condition is concrete
			return br.executeConcrete(ti);
		}
		
		int v1 = sf.peek(0);
		int v2 = sf.peek(1);
		
		// the condition is abstract
		System.out.printf("%s> Values: %d (%s) %d (%s)\n", name, v2, abs_v2, v1, abs_v1);

		AbstractBoolean abs_condition = br.getCondition(abs_v1, abs_v2);

		if (abs_condition == AbstractBoolean.TRUE) {
			conditionValue = true;
		} else if (abs_condition == AbstractBoolean.FALSE) {
			conditionValue = false;
		} else { // TOP
			if (!ti.isFirstStepInsn()) { // first time around
				ChoiceGenerator<?> cg = new AbstractChoiceGenerator();
				ss.setNextChoiceGenerator(cg);

				return br.getSelf();
			} else { // this is what really returns results
				ChoiceGenerator<?> cg = ss.getChoiceGenerator();
					
				assert (cg instanceof AbstractChoiceGenerator) : "expected AbstractChoiceGenerator, got: " + cg;
				
				conditionValue = (Integer) cg.getNextChoice() == 0 ? false : true;
			}
		}

		System.out.println(name + "> Result: " + conditionValue);
		
		sf.pop();
		sf.pop();
		
		return (conditionValue ? br.getTarget() : br.getNext(ti));
	}
	
	private AbstractValue getAbstractValue(StackFrame sf, int index) {
		Attribute attr = (Attribute)sf.getOperandAttr(index);
		
		if (attr != null) {
			return attr.abstractValue;
		}
		
		return null;
	}
	
	private AbstractValue getLeftAbstractValue(StackFrame sf) {
		return getAbstractValue(sf, 0);
	}
	
	private AbstractValue getRightAbstractValue(StackFrame sf) {
		return getAbstractValue(sf, 1);
	}
}