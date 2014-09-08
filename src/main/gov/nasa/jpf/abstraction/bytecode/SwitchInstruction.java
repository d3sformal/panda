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
 *
 */
public abstract class SwitchInstruction extends gov.nasa.jpf.jvm.bytecode.SwitchInstruction {

    protected SwitchInstruction(int defaultTarget, int numberOfTargets) {
        super(defaultTarget, numberOfTargets);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();
        Expression expr = ExpressionUtil.getExpression(sf.getOperandAttr());

        if (!ti.isFirstStepInsn()) {

            ArrayList<Integer> choices = null;

            if (expr != null && RunDetector.isRunning()) {
                ArrayList<Integer> choiceCandidates = new ArrayList<Integer>();
                boolean predicateAbstractionFailed = false;

                for (int i = 0; i < matches.length; i++) {
                    int match = matches[i];

                    TruthValue pred = PredicateAbstraction.getInstance().processBranchingCondition(getPosition(), Equals.create(expr, Constant.create(match)));

                    if (pred == TruthValue.UNDEFINED) {
                        predicateAbstractionFailed = true;
                        break;
                    }

                    if (pred != TruthValue.FALSE) {
                        choiceCandidates.add(i);
                    }
                }

                if (!predicateAbstractionFailed) {
                    choices = choiceCandidates;
                }
            }

            if (choices == null) {
                return super.execute(ti);
            }

            if (choices.size() > 1) {
                int[] param = new int[choices.size()];
                for (int i = 0; i < choices.size(); ++i)
                    param[i] = choices.get(i);
                ChoiceGenerator<?> cg = new IntChoiceFromList("abstractSwitchAll", param);
                ss.setNextChoiceGenerator(cg);
                return this;
            } else if (choices.size() == 1) {
                sf.pop();

                PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(Equals.create(expr, Constant.create(matches[choices.get(0)])), getMethodInfo(), getPosition());

                return mi.getInstructionAt(targets[choices.get(0)]);
            } else {
                sf.pop();

                // for all x in matches: expr != x
                Predicate constraint = Tautology.create();

                for (int idx = 0; idx < matches.length; ++idx) {
                    constraint = Conjunction.create(constraint, Negation.create(Equals.create(expr, Constant.create(matches[idx]))));
                }

                PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(constraint, TruthValue.TRUE), getMethodInfo(), mi.getInstructionAt(target).getPosition());

                return mi.getInstructionAt(target);
            }
        } else {
            ChoiceGenerator<?> cg = ss.getCurrentChoiceGenerator("abstractSwitchAll", IntChoiceFromList.class);
            int idx = ((IntChoiceFromList) cg).getNextChoice();
            sf.pop();

            if (idx == DEFAULT) {
                return mi.getInstructionAt(target);
            }

            lastIdx = idx;

            PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(Equals.create(expr, Constant.create(matches[idx])), TruthValue.TRUE), getMethodInfo(), mi.getInstructionAt(targets[idx]).getPosition());

            return mi.getInstructionAt(targets[idx]);
        }
    }

}
