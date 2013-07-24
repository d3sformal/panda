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
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

public class BinaryIfInstructionExecutor {

	final public Instruction execute(AbstractBranching br, ThreadInfo ti) {
		
		String name = br.getClass().getSimpleName();

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		Attribute attr1 = (Attribute) sf.getOperandAttr();
		Attribute attr2 = (Attribute) sf.getOperandAttr();
		
		if (attr1 == null) attr1 = new EmptyAttribute();
		if (attr2 == null) attr2 = new EmptyAttribute();

		AbstractValue abs_v1 = attr1.getAbstractValue();
		AbstractValue abs_v2 = attr2.getAbstractValue();
		Expression expr1 = attr1.getExpression();
		Expression expr2 = attr2.getExpression();
		
		AbstractBoolean abs_condition = null;
		
		// PREDICATE ABSTRACTION
		if (expr1 != null) {
			TruthValue pred = GlobalAbstraction.getInstance().evaluatePredicate(br.createPredicate(expr1, expr2));

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
				System.out.printf("%s> Predicate: %s = 0\n", name, expr1.toString(AccessPath.NotationPolicy.DOT_NOTATION));
			}
		}		

		if (abs_condition == null) {
			
			if (abs_v1 == null && abs_v2 == null) { // the condition is concrete
                return br.executeConcrete(ti);
			}
        
			int v1 = sf.peek(0);
			int v2 = sf.peek(1);
		
			// the condition is abstract
			System.out.printf("%s> Values: %d (%s) %d (%s)\n", name, v2, abs_v2, v1, abs_v1);

			// NUMERIC ABSTRACTION
			abs_condition = br.getCondition(v1, abs_v1, v2, abs_v2);
		}

		boolean conditionValue;
		
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
}
