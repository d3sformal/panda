package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.AbstractInstructionFactory;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
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
		Attribute attr = (Attribute) sf.getOperandAttr();
		
		AbstractValue abs_v = null;
		Expression expr = null;
		
		if (attr == null) attr = new EmptyAttribute();

		abs_v = attr.getAbstractValue();
		expr = attr.getExpression();
		
		if (expr != null) {
			switch (AbstractInstructionFactory.abs.processBranching(Equals.create(expr, Constant.create(0)))) {
			case TRUE:
				sf.pop();
				
				return br.getTarget();
			case FALSE:
				sf.pop();
				
				return br.getNext(ti);
			case UNKNOWN:
				ChoiceGenerator<?> cg = new AbstractChoiceGenerator();
				ss.setNextChoiceGenerator(cg);
				
				attr.setExpression(null);

				return br.getSelf();
			case UNDEFINED:
				break;
			}
		}
		
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
}
