package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
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
 * Implementation of all binary IF instructions regardless their precise type.
 */
public class BinaryIfInstructionExecutor {

	final public Instruction execute(AbstractBranching br, ThreadInfo ti) {
		
		String name = br.getClass().getSimpleName();

		SystemState ss = ti.getVM().getSystemState();
		StackFrame sf = ti.getModifiableTopFrame();
		Attribute attr1 = (Attribute) sf.getOperandAttr(1);
		Attribute attr2 = (Attribute) sf.getOperandAttr(0);
		
		attr1 = Attribute.ensureNotNull(attr1);
		attr2 = Attribute.ensureNotNull(attr2);

		AbstractValue abs_v1 = attr1.getAbstractValue();
		AbstractValue abs_v2 = attr2.getAbstractValue();
		Expression expr1 = attr1.getExpression();
		Expression expr2 = attr2.getExpression();
		
		AbstractBoolean abs_condition = null;
		
		boolean conditionValue;

        int v1 = sf.peek(1);
        int v2 = sf.peek(0);

		/**
		 * First we check whether there is no choice generator present
		 * If not we evaluate the branching condition
		 * Otherwise we inspect all the choices
		 */
		if (!ti.isFirstStepInsn()) { // first time around
			/**
			 * If there is enough information (symbolic expressions) to decide the condition we ask abstractions to provide the truth value
			 * Only predicate abstraction is designed to respond with a valid value (TRUE, FALSE, UNKNOWN).
			 * No other abstraction can do that, the rest of them returns UNDEFINED.
			 */
			if (expr1 != null && expr2 != null && RunDetector.isRunning()) {
				Predicate predicate = br.createPredicate(expr1, expr2);
				TruthValue truth = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(predicate);
	
				switch (truth) {
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
			}		
	
			// IF THE abs_condition COULD NOT BE DERIVED BY PREDICATE ABSTRACTION (IT IS NOT ACTIVE)
			if (abs_condition == null) {
				if (abs_v1 == null && abs_v2 == null) { // the condition is concrete
	                return br.executeConcrete(ti);
				}
	        
				// the condition is abstract
	
				// NUMERIC ABSTRACTION
				abs_condition = br.getCondition(v1, abs_v1, v2, abs_v2);
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

			if (expr1 != null && expr2 != null) {
				Predicate predicate = br.createPredicate(expr1, expr2);
				GlobalAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.create(conditionValue)));
			}
		}
		
		sf.pop();
		sf.pop();

        if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.pruning")) {
            if (br.getConcreteBranch(v1, v2) != TruthValue.create(conditionValue)) {
                System.err.println("[WARNING] Inconsistent concrete and abstract branching: " + br.createPredicate(expr1, expr2));
                ss.setIgnored(true);
            }
        }

		return (conditionValue ? br.getTarget() : br.getNext(ti));
	}
}
