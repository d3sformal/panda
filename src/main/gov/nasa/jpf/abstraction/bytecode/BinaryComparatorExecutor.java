package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * An implementation of common behaviour of all the comparison expressions
 */
public abstract class BinaryComparatorExecutor<T> {
    final public Instruction execute(AbstractBinaryOperator<T, T> cmp, ThreadInfo ti) {

        String name = cmp.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        Expression expr1 = getFirstOperandExpression(sf);
        Expression expr2 = getSecondOperandExpression(sf);

        Integer result = null;
        boolean less_than = false;
        boolean equal = false;
        boolean greater_than = false;

        /**
         * First we check whether there is no choice generator present
         * If not we perform the comparison
         * Otherwise we inspect all the choices
         */
        if (!ti.isFirstStepInsn()) { // first time around
            /**
             * If there is enough information (symbolic expressions) to decide the condition we ask abstractions to provide the truth value
             * Only predicate abstraction is designed to respond with a valid value (TRUE, FALSE, UNKNOWN).
             * No other abstraction can do that, the rest of them returns UNDEFINED.
             */
            if (expr1 != null && expr2 != null && RunDetector.isRunning()) {
                TruthValue lt = PredicateAbstraction.getInstance().processBranchingCondition(cmp.getSelf().getPosition(), LessThan.create(expr1, expr2));
                TruthValue eq = PredicateAbstraction.getInstance().processBranchingCondition(cmp.getSelf().getPosition(), Equals.create(expr1, expr2));
                TruthValue gt = null;

                if (TruthValue.and(lt, eq) == TruthValue.UNDEFINED) {
                    gt = TruthValue.UNDEFINED;
                } else if (TruthValue.and(lt, eq) == TruthValue.FALSE) {
                    gt = TruthValue.TRUE;
                } else if (lt == TruthValue.TRUE || eq == TruthValue.TRUE) {
                    gt = TruthValue.FALSE;
                } else {
                    gt = TruthValue.UNKNOWN;
                }

                // UNDEFINED MEANS THERE WAS NO ABSTRACTION TO DECIDE THE VALIDITY OF THE PREDICATE
                if (gt != TruthValue.UNDEFINED) {
                    less_than = lt != TruthValue.FALSE;
                    equal = eq != TruthValue.FALSE;
                    greater_than = gt != TruthValue.FALSE;

                    if (less_than) result = -1;
                    if (equal) result = 0;
                    if (greater_than) result = +1;
                }
            }

            /**
             * When there was no predicate abstraction we default to concrete execution
             */
            if (result == null) {
                Instruction ret = cmp.executeConcrete(ti);

                sf.setOperandAttr(Constant.create(sf.peek()));

                return ret;
            }

            /**
             * If the result of the comparison is not deterministic we create a choice generator and let JPF to reexecute this instruction
             */
            if ((less_than && equal) || (less_than && greater_than) || (equal && greater_than)) {
                int size = 0;

                size += less_than ? 1 : 0;
                size += equal ? 1 : 0;
                size += greater_than ? 1 : 0;

                int i = 0;
                int[] choices = new int[size];

                if (less_than) {
                    choices[i] = -1;
                    ++i;
                }

                if (equal) {
                    choices[i] = 0;
                    ++i;
                }

                if (greater_than) {
                    choices[i] = +1;
                    ++i;
                }

                ChoiceGenerator<?> cg = new IntChoiceFromList("abstractComparisonAll", choices);
                ss.setNextChoiceGenerator(cg);

                return cmp.getSelf();
            } else if (less_than) {
                PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(LessThan.create(expr1, expr2), cmp.getSelf().getMethodInfo(), cmp.getNext(ti).getPosition());
            } else if (equal) {
                PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(Equals.create(expr1, expr2), cmp.getSelf().getMethodInfo(), cmp.getNext(ti).getPosition());
            } else if (greater_than) {
                PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(LessThan.create(expr2, expr1), cmp.getSelf().getMethodInfo(), cmp.getNext(ti).getPosition());
            }
        } else { // this is what really returns results
            /**
             * Exploration of all possible outcomes of the comparison
             */
            ChoiceGenerator<?> cg = ss.getChoiceGenerator();

            assert (cg instanceof IntChoiceFromList);

            result = (Integer) cg.getNextChoice();

            if (expr1 != null && expr2 != null) {
                Predicate predicate = Equals.create(expr1, expr2);

                if (result == -1) {
                    predicate = LessThan.create(expr1, expr2);
                }
                if (result == +1) {
                    predicate = LessThan.create(expr2, expr1);
                }

                PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.TRUE), cmp.getSelf().getMethodInfo(), cmp.getNext(ti).getPosition());
            }
        }

        storeResult(Constant.create(result), sf);

        return cmp.getNext(ti);
    }

    protected Expression getOperandExpression(StackFrame sf, int index) {
        return ExpressionUtil.getExpression(sf.getOperandAttr(index));
    }

    abstract protected Expression getFirstOperandExpression(StackFrame sf);
    abstract protected Expression getSecondOperandExpression(StackFrame sf);
    abstract protected T getFirstOperand(StackFrame sf);
    abstract protected T getSecondOperand(StackFrame sf);
    abstract protected void storeResult(Expression result, StackFrame sf);
}
