/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;

public class JPF_gov_nasa_jpf_abstraction_LazyRefinementTest extends NativePeer {
    private static int curChoice;

    @MJI
    public void createChoices__I__V(MJIEnv env, int clsObjRef, int choices) {
        ThreadInfo ti = env.getThreadInfo();

        if (!ti.isFirstStepInsn()) {
            IntIntervalGenerator cg = new IntIntervalGenerator("choices", 0, choices);

            if (env.setNextChoiceGenerator(cg)) {
                env.repeatInvocation();
            }
        } else {
            IntIntervalGenerator cg = (IntIntervalGenerator) env.getChoiceGenerator();

            curChoice = cg.getNextChoice();
        }
    }

    @MJI
    public void error__I__V(MJIEnv env, int clsObjRef, int choice) {
        if (curChoice == choice && PredicateAbstraction.getInstance().getNumberOfRefinements() == 0) {
            Predicate p1 = Equals.create(DefaultRoot.create("x"), Constant.create(0));
            Predicate p2 = LessThan.create(DefaultRoot.create("x"), Constant.create(0));

            PredicateAbstraction.getInstance().getTraceFormula().markCallInvoked(env.getMethodInfo());
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(p1, env.getMethodInfo(), 0);
            PredicateAbstraction.getInstance().getTraceFormula().markCallStarted();
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(p2, env.getMethodInfo(), 1);
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(Tautology.create(), env.getMethodInfo(), 2, true);
            PredicateAbstraction.getInstance().getTraceFormula().markReturn();
            PredicateAbstraction.getInstance().getTraceFormula().markReturned();

            env.throwException("java.lang.RuntimeException");
        }
    }
}
