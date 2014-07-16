package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
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

    private Constant constant;

    public UnaryIfInstructionExecutor(Constant constant) {
        this.constant = constant;
    }

    final public Instruction execute(AbstractBranching br, ThreadInfo ti) {

        String name = br.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();
        Expression expr = ExpressionUtil.getExpression(sf.getOperandAttr());

        TruthValue condition = null;

        boolean conditionValue;

        int v1 = sf.peek(0);
        int v2 = constant.value.intValue();

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
                condition = PredicateAbstraction.getInstance().processBranchingCondition(br.createPredicate(expr, constant));
            }

            // In case there was no predicate abstraction / no symbolic expression (or the execution did not yet reach the target program or we already left it)
            if (condition == null) {
                // fallback to a concrete execution
                return br.executeConcrete(ti);
            }

            if (condition == TruthValue.TRUE) {
                ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
                conditionValue = true;
            } else if (condition == TruthValue.FALSE) {
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
                Predicate predicate = br.createPredicate(expr, constant);
                PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.create(conditionValue)));
            }
        }

        sf.pop();

        if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.prune_infeasible")) {
            if (br.getConcreteBranchValue(v1, constant.value.intValue()) != TruthValue.create(conditionValue)) {
                System.err.println("[WARNING] Inconsistent concrete and abstract branching: " + br.createPredicate(expr, constant));
                ss.setIgnored(true);
            }
        }

        return (conditionValue ? br.getTarget() : br.getNext(ti));
    }
}
