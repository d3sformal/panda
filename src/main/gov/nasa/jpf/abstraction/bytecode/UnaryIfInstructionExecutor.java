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

public class UnaryIfInstructionExecutor {

	final public Instruction execute(AbstractBranching br, ThreadInfo ti) {
		
		String name = br.getClass().getName();

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		AbstractValue abs_v = getAbstractValue(sf);
		
		boolean conditionValue;

		if (abs_v == null) { // the condition is concrete
			return br.executeConcrete(ti);
		}
		
		// the condition is abstract
		System.out.printf("%s> Values: %d (%s)\n", name, sf.peek(0), abs_v);

		AbstractBoolean abs_condition = br.getCondition(0, abs_v, 0, null);

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
		
		return (conditionValue ? br.getTarget() : br.getNext(ti));
	}
	
	public AbstractValue getAbstractValue(StackFrame sf) {
		Attribute attr = (Attribute)sf.getOperandAttr();
		
		if (attr != null) {
			return attr.getAbstractValue();
		}
		
		return null;
	}
}
