package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.choice.BreakGenerator;

import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.BranchingExecutionHelper;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Implementation of all unary IF instructions regardless their precise type.
 */
public class UnaryIfInstructionExecutor {

    private Constant constant;
    private boolean conditionValue;


    public UnaryIfInstructionExecutor(Constant constant) {
        this.constant = constant;
    }

    final public Instruction execute(AbstractBranching br, ThreadInfo ti) {

        String name = br.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();
        Expression expr = ExpressionUtil.getExpression(sf.getOperandAttr());

        Predicate predicate = null;
        TruthValue condition = TruthValue.UNDEFINED;

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
                predicate = br.createPredicate(expr, constant);

                if (expr instanceof ObjectFieldRead) {
                    ObjectFieldRead fr = (ObjectFieldRead) expr;
                    AccessExpression o = fr.getObject();
                    Field f = fr.getField();

                    if (o instanceof PackageAndClass) {
                        if (f.getName().equals("$assertionsDisabled")) {
                            condition = VM.getVM().getJPF().getConfig().getEnum("panda.assertions_disabled", TruthValue.class.getEnumConstants(), TruthValue.UNDEFINED);
                        }
                    }
                }

                if (condition == TruthValue.UNDEFINED) {
                    condition = PredicateAbstraction.getInstance().processBranchingCondition(br.getSelf().getPosition(), predicate);
                }
            }

            switch (condition) {
                // IF THE BRANCH COULD NOT BE PICKED BY PREDICATE ABSTRACTION (IT IS NOT ACTIVE)
                default:
                case UNDEFINED:
                    return br.executeConcrete(ti);
                case TRUE:
                    ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
                    conditionValue = true;

                    PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(predicate, br.getSelf().getMethodInfo(), br.getDefaultTarget().getPosition());

                    return br.getSelf();
                case FALSE:
                    ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
                    conditionValue = false;

                    PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(Negation.create(predicate), br.getSelf().getMethodInfo(), br.getNext(ti).getPosition());

                    return br.getSelf();
                case UNKNOWN:
                    ChoiceGenerator<?> cg = new AbstractChoiceGenerator();
                    ss.setNextChoiceGenerator(cg);

                    return br.getSelf();
            }
        } else { // this is what really returns results
            ChoiceGenerator<?> cg = ss.getChoiceGenerator();

            if (cg instanceof AbstractChoiceGenerator) {
                conditionValue = (Integer) cg.getNextChoice() == 0 ? false : true;

                if (expr != null) {
                    predicate = br.createPredicate(expr, constant);
                    PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.create(conditionValue)), br.getSelf().getMethodInfo(), br.getTarget(ti, conditionValue ? 1 : 0).getPosition());
                }
            } else if (!(cg instanceof BreakGenerator)) {
                throw new RuntimeException("expected AbstractChoiceGenerator, got: " + cg);
            }
        }

        sf.pop();

        Predicate branchCondition = br.createPredicate(expr, constant);

        if (!conditionValue) {
            branchCondition = Negation.create(branchCondition);
        }

        BranchingExecutionHelper.synchronizeConcreteAndAbstractExecutions(ti, branchCondition, br.getConcreteBranchValue(v1, constant.value.intValue()), conditionValue, br.getTarget(ti, conditionValue ? 1 : 0), br.getSelf());

        return br.getTarget(ti, conditionValue ? 1 : 0);
    }
}
