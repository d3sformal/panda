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

import java.util.List;
import java.util.Map;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.BreakGenerator;

import gov.nasa.jpf.abstraction.DynamicIntChoiceGenerator;
import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.Unknown;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class JPF_gov_nasa_jpf_abstraction_Verifier extends NativePeer {
    @MJI
    public static int unknownInt____I(MJIEnv env, int clsObjRef) {
        return unknown(env, clsObjRef);
    }

    @MJI
    public static int unknownNonNegativeInt____I(MJIEnv env, int clsObjRef) {
        int ret = unknown(env, clsObjRef);

        if (!env.isInvocationRepeated() && !env.getSystemState().isIgnored()) {
            StackFrame before = env.getCallerStackFrame();
            MethodInfo caller = before.getMethodInfo();
            int pc = before.getPC().getPosition() + before.getPC().getLength();

            Predicate constraint = LessThan.create(Constant.create(-1), ExpressionUtil.getExpression(env.getReturnAttribute()));

            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(constraint, caller, pc); // Make sure refinement (trace feasibility check, interpolation) is aware of this constraint
            PredicateAbstraction.getInstance().getPredicateValuation().force(constraint, TruthValue.TRUE); // Make sure that pre-existing predicates about unknown are propagated
        }

        return ret < 0 ? -ret : ret;
    }

    @MJI
    public static char unknownChar____C(MJIEnv env, int clsObjRef) {
        return (char) unknown(env, clsObjRef);
    }

    @MJI
    public static boolean unknownBool____Z(MJIEnv env, int clsObjRef) {
        return unknown(env, clsObjRef) < 0;
    }

    public static int unknown(MJIEnv env, int clsObjRef) {
        ThreadInfo ti = env.getThreadInfo();

        if (!ti.isFirstStepInsn()) { // first time around
            DynamicIntChoiceGenerator cg = new DynamicIntChoiceGenerator("unknownInt()", new int[] { 0 }); // Fixed default value

            cg.setInsn(env.getInstruction());

            // During PRUNING (with FORCE_FEASIBLE)
            // at some point we reach a feasible branch which is not enabled under current concrete execution
            // we generate a model of all unknown values
            // we backtrack and try different choices

            String name = ((Root) PredicateAbstraction.getInstance().getSSAManager().getSymbolIncarnation(Unknown.create(ti.getPC(), cg), 0)).getName();
            Map<String, Unknown> unknowns = PredicateAbstraction.getInstance().getUnknowns();
            Integer[] prevValues = new Integer[0];
            List<TraceFormula> prevTraces = null;

            /*
             * Eagerly reuse already discovered values of a specific unknown from different subtrees.
             * This helps save a lot of computational effort when a tuple of values is found together for several unknowns:
             *   (u1=1, u2=7, u3=8)
             * once we backtrack past u1 all the generators are actually forgotten and we need to continue to
             *
             *   (u1=1, u2=0, u3=0)
             *
             * before introducing values (u2=7, u3=8); after backtrack we again continue to
             *
             *   (u1=1, u2=7, u3=0)
             *
             * where we finally introduce u3=8.
             *
             * But if we copied the old choices of u2 and u3 whenever we encounter them in a new subtree of the state space, we can avoid the need to search for the values.
             * However, because we copy the whole domain of u2 and u3 they again contain the initial value 0 and re-explore the subtree anyway, even though by finding the values 7, 8 we gain nothing as the values are already included (copied).
             * We may actually copy a lot more than we need to, making Panda explore many more subtrees.
             *
             * Furthermore, if Panda does not produce tuples of values, this does not help at all.
             */
            List<Map<String, Integer>> prevConditions = null;

            if (unknowns.containsKey(name)) {
                prevValues = unknowns.get(name).getChoiceGenerator().getChoices();
                prevTraces = unknowns.get(name).getChoiceGenerator().getTraces();
                prevConditions = unknowns.get(name).getChoiceGenerator().getConditions();
            }

            int numProcessedChoices = 0;
            if (unknowns.containsKey(name)) numProcessedChoices = unknowns.get(name).getChoiceGenerator().getProcessedNumberOfChoices();

            for (int i = numProcessedChoices; i < prevValues.length; ++i) {
                cg.add(prevValues[i], prevTraces.get(i - 1), prevConditions.get(i - 1));
            }

            if (env.setNextChoiceGenerator(cg)) {
                env.getSystemState().setForced(true);
                env.repeatInvocation();
            }

            return 0; // Fixed Dummy Value
        } else {
            ChoiceGenerator<?> cg = env.getChoiceGenerator();

            assert (cg instanceof DynamicIntChoiceGenerator) : "expected DynamicIntChoiceGenerator, got: " + cg;

            DynamicIntChoiceGenerator icg = (DynamicIntChoiceGenerator) cg;

            // Check condition and break the transition if it does not hold
            if (!icg.isNextChoiceEnabled()) {
                String message = "The model of this unknown has not been explicitely enabled under the current combination of all the preceding unknowns";
                //cg = new BreakGenerator(message, ThreadInfo.getCurrentThread(), false);

                if (PandaConfig.getInstance().enabledVerbose(JPF_gov_nasa_jpf_abstraction_Verifier.class)) {
                    System.out.println();
                    System.out.println();
                    System.out.println("[DEBUG] " + message);
                    System.out.println();
                    System.out.println();
                }

                env.getSystemState().setIgnored(true);

                return 0;
            }

            Unknown unknown = Unknown.create(ti.getPC(), icg);

            PredicateAbstraction.getInstance().registerUnknown(unknown);

            env.setReturnAttribute(unknown);

            return icg.getNextChoice();
        }
    }
}
