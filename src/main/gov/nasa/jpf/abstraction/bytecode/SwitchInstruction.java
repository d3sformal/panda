//
// Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.abstraction.bytecode;

import java.util.ArrayList;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

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

                    TruthValue pred = PredicateAbstraction.getInstance().processBranchingCondition(Equals.create(expr, Constant.create(match)));

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

            if (choices.size() > 0) {
                int[] param = new int[choices.size()];
                for (int i = 0; i < choices.size(); ++i)
                    param[i] = choices.get(i);
                ChoiceGenerator<?> cg = new IntChoiceFromList("abstractSwitchAll", param);
                ss.setNextChoiceGenerator(cg);
                return this;
            } else {
                sf.pop();
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

            PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(Equals.create(expr, Constant.create(matches[idx])), TruthValue.TRUE));

            return mi.getInstructionAt(targets[idx]);
        }
    }

}
