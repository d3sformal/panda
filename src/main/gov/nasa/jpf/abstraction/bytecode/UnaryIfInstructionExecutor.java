package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

public class UnaryIfInstructionExecutor {

	final public Instruction execute(AbstractBranching br, ThreadInfo ti) {
		
		String name = br.getClass().getSimpleName();

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		Attribute attr = (Attribute) sf.getOperandAttr();
		
		if (attr == null) attr = new EmptyAttribute();

		AbstractValue abs_v = attr.getAbstractValue();
		Expression expr = attr.getExpression();
		
		AbstractBoolean abs_condition = null;
		
		boolean conditionValue;
		
		if (!ti.isFirstStepInsn()) { // first time around
		
			// PREDICATE ABSTRACTION
			if (expr != null) {				
				TruthValue pred = GlobalAbstraction.getInstance().evaluatePredicate(br.createPredicate(expr, Constant.create(0)));
	
				switch (pred) {
				case TRUE:
					abs_condition = AbstractBoolean.TRUE;
					break;
				case FALSE:
					abs_condition = AbstractBoolean.FALSE;
					break;
				case UNKNOWN:
					abs_condition = AbstractBoolean.TOP;
					break;
				}
							
				if (pred != TruthValue.UNDEFINED) {
					System.out.printf("%s> Predicate: %s = 0\n", name, expr.toString(AccessPath.NotationPolicy.DOT_NOTATION));
				}
			}		
	
			if (abs_condition == null) {
				if (abs_v == null) { // the condition is concrete
					return br.executeConcrete(ti);
				}
			
				// the condition is abstract
				System.out.printf("%s> Values: %d (%s)\n", name, sf.peek(0), abs_v);
	
				// NUMERIC ABSTRACTION
				abs_condition = br.getCondition(0, abs_v, 0, null);
			}
			
			if (abs_condition == AbstractBoolean.TRUE) {
				conditionValue = true;
			} else if (abs_condition == AbstractBoolean.FALSE) {
				conditionValue = false;
			} else { // TOP	
				ChoiceGenerator<?> cg = new AbstractChoiceGenerator();
				ss.setNextChoiceGenerator(cg);

				return br.getSelf();
			}
		} else { // this is what really returns results
			ChoiceGenerator<?> cg = ss.getChoiceGenerator();
				
			assert (cg instanceof AbstractChoiceGenerator) : "expected AbstractChoiceGenerator, got: " + cg;
			
			conditionValue = (Integer) cg.getNextChoice() == 0 ? false : true;
		}

		System.out.println(name + "> Result: " + conditionValue);
		
		sf.pop();
		
		return (conditionValue ? br.getTarget() : br.getNext(ti));
	}
}
