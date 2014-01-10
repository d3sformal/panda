package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Implementation of all unary IF instructions regardless their precise type.
 */
public class UnaryIfInstructionExecutor {

	final public Instruction execute(AbstractBranching br, ThreadInfo ti) {
		
		String name = br.getClass().getSimpleName();

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		Attribute attr = (Attribute) sf.getOperandAttr();
		
		attr = Attribute.ensureNotNull(attr);

		AbstractValue abs_v = attr.getAbstractValue();
		Expression expr = attr.getExpression();
		
		AbstractBoolean abs_condition = null;
		
		boolean conditionValue;
		
		/**
		 * First we check whether there is no choice generator present
		 * If not we evaluate the branching condition
		 * Otherwise we inspect all the choices
		 */
		if (!ti.isFirstStepInsn()) { // first time around
			/**
			 * If there is enough information (symbolic expression) to decide the condition we ask abstractions to provide the truth value
			 * Only predicate abstraction is designed to respond with a valid value (TRUE, FALSE, UNKNOWN).
			 * No other abstraction can do that, the rest of them returns UNDEFINED.
			 */
			if (expr != null && RunDetector.isRunning()) {
				TruthValue pred = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(br.createPredicate(expr, Constant.create(0)));
	
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
					System.out.printf("%s> Predicate: %s = 0\n", name, expr.toString(Notation.DOT_NOTATION));
				}
			}		
	
			// In case there was no predicate abstraction / no symbolic expression (or the execution did not yet reach the target program or we already left it)
			if (abs_condition == null) {
				// In case there is no abstract value fallback to a concrete execution
				if (abs_v == null) { // the condition is concrete
					return br.executeConcrete(ti);
				}
			
				// the condition is abstract
				System.out.printf("%s> Values: %d (%s)\n", name, sf.peek(0), abs_v);
	
				// NUMERIC ABSTRACTION
				abs_condition = br.getCondition(0, abs_v, 0, null);
			}
			
			if (abs_condition == AbstractBoolean.TRUE) {
                ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
				conditionValue = true;
			} else if (abs_condition == AbstractBoolean.FALSE) {
                ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
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
			
			if (expr != null) {
				Predicate predicate = br.createPredicate(expr, Constant.create(0));
			
				GlobalAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.create(conditionValue)));
			}
		}

		System.out.println(name + "> Result: " + conditionValue);
		
		sf.pop();
		
		return (conditionValue ? br.getTarget() : br.getNext(ti));
	}
}
