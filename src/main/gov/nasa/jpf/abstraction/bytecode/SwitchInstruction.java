package gov.nasa.jpf.abstraction.bytecode;

import java.util.ArrayList;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.BranchingExecutionHelper;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * common root class for LOOKUPSWITCH and TABLESWITCH insns
ThreadInfo ti,  *
 */
public abstract class SwitchInstruction extends gov.nasa.jpf.jvm.bytecode.SwitchInstruction implements AbstractBranching {

    protected SwitchInstruction(int defaultTarget, int numberOfTargets) {
        super(defaultTarget, numberOfTargets);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();
        Expression expr = ExpressionUtil.getExpression(sf.getOperandAttr());
        Integer index = null;

        if (!ti.isFirstStepInsn()) {

            ArrayList<Integer> choices = null;

            if (expr != null && RunDetector.isRunning()) {
                Predicate defaultConstraint = Tautology.create();
                ArrayList<Integer> choiceCandidates = new ArrayList<Integer>();
                boolean predicateAbstractionFailed = false;

                for (int i = 0; i < matches.length; i++) {
                    int match = matches[i];
                    Predicate matchPred = Equals.create(expr, Constant.create(match));

                    TruthValue absVal = PredicateAbstraction.getInstance().processBranchingCondition(getPosition(), matchPred);

                    if (absVal == TruthValue.UNDEFINED) {
                        predicateAbstractionFailed = true;
                        break;
                    }

                    if (absVal != TruthValue.FALSE) {
                        choiceCandidates.add(i);
                    }

                    defaultConstraint = Conjunction.create(defaultConstraint, Negation.create(matchPred));
                }

                TruthValue absVal = PredicateAbstraction.getInstance().processBranchingCondition(getPosition(), defaultConstraint);

                if (absVal == TruthValue.UNDEFINED) {
                    predicateAbstractionFailed = true;
                } else if (absVal != TruthValue.FALSE) {
                    choiceCandidates.add(DEFAULT);
                }

                if (!predicateAbstractionFailed) {
                    choices = choiceCandidates;
                }
            }

            if (choices == null) {
                return executeConcrete(ti);
            }

            if (choices.size() > 1) {
                int[] param = new int[choices.size()];
                for (int i = 0; i < choices.size(); ++i)
                    param[i] = choices.get(i);
                ChoiceGenerator<?> cg = new IntChoiceFromList("abstractSwitchAll", param);
                ss.setNextChoiceGenerator(cg);
                return this;
            } else if (choices.size() == 1) {
                index = choices.get(0);
            } else {
                index = DEFAULT;
            }
        } else {
            ChoiceGenerator<?> cg = ss.getCurrentChoiceGenerator("abstractSwitchAll", IntChoiceFromList.class);
            index = ((IntChoiceFromList) cg).getNextChoice();
            lastIdx = index;
        }

        int v = sf.peek();
        sf.pop();

        if (index == DEFAULT) {
            Predicate constraint = Tautology.create();
            boolean concreteJump = true;

            // for all x in matches: expr != x
            for (int idx = 0; idx < matches.length; ++idx) {
                constraint = Conjunction.create(constraint, Negation.create(Equals.create(expr, Constant.create(matches[idx]))));

                concreteJump &= (v != matches[idx]);
            }

            PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(constraint, TruthValue.TRUE), getMethodInfo(), mi.getInstructionAt(target).getPosition());

            BranchingExecutionHelper.synchronizeConcreteAndAbstractExecutions(this, ti, constraint, concreteJump, true, index);
        } else {
            Predicate constraint = Equals.create(expr, Constant.create(matches[index]));

            PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(constraint, TruthValue.TRUE), getMethodInfo(), mi.getInstructionAt(targets[index]).getPosition());

            BranchingExecutionHelper.synchronizeConcreteAndAbstractExecutions(this, ti, constraint, getConcreteBranchValue(v, matches[index]), true, index);
        }

        return getTarget(ti, index);
    }

    @Override
    public boolean getConcreteBranchValue(int v1, int v2) {
        return v1 == v2;
    }

    @Override
    public Predicate createPredicate(Expression expr1, Expression expr2) {
        return Equals.create(expr1, expr2);
    }

    @Override
    public Instruction getDefaultTarget() {
        return mi.getInstructionAt(target);
    }

    @Override
    public Instruction getSelf() {
        return this;
    }

    @Override
    public Instruction getTarget(ThreadInfo ti, int num) {
        if (num == DEFAULT) {
            return mi.getInstructionAt(target);
        }

        return mi.getInstructionAt(targets[num]);
    }

}
