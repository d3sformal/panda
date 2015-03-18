package gov.nasa.jpf.abstraction;

import java.util.List;
import java.util.Map;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.BreakGenerator;

import gov.nasa.jpf.abstraction.DynamicIntChoiceGenerator;
import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.Unknown;

public class JPF_gov_nasa_jpf_abstraction_Verifier extends NativePeer {
    @MJI
    public static int unknownInt____I(MJIEnv env, int clsObjRef) {
        return unknown(env, clsObjRef);
    }

    @MJI
    public static int unknownPositiveInt____I(MJIEnv env, int clsObjRef) {
        int ret = unknown(env, clsObjRef);

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

            // During PRUNING (with FORCE_FEASIBLE)
            // at some point we reach a feasible branch which is not enabled under current concrete execution
            // we generate a model of all unknown values
            // we backtrack and try different choices
            //
            // what happens:
            // suppose the only enabling pair of unknown values is u1=1, u2=1
            // we need to backtrack back to u1 (choice generator appearing first)
            // at that point we have dropped u2
            // we reintroduce a new generator for u2 which does not have the correct value
            //
            // question: why the hell does it not suffice to wait for the exploration to reach the disabled branch and at that point come up with the new value for u2
            // if we are unlucky it might come up with a different model (even for u1, which would cause further backtracking)
            // -> divergence
            String name = ((Root) PredicateAbstraction.getInstance().getSSAManager().getSymbolIncarnation(Unknown.create(ti.getPC(), cg), 0)).getName();
            Map<String, Unknown> unknowns = PredicateAbstraction.getInstance().getUnknowns();
            Integer[] prevValues = new Integer[0];
            List<TraceFormula> prevTraces = null;
            List<Map<String, Integer>> prevConditions = null;

            if (unknowns.containsKey(name)) {
                prevValues = unknowns.get(name).getChoiceGenerator().getChoices();
                prevTraces = unknowns.get(name).getChoiceGenerator().getTraces();
                prevConditions = unknowns.get(name).getChoiceGenerator().getConditions();
            }

            for (int i = 1; i < prevValues.length; ++i) {
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
                cg = new BreakGenerator(message, ThreadInfo.getCurrentThread(), false);

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
