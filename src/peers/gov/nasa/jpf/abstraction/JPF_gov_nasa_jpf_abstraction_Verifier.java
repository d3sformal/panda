package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.IntChoiceGenerator;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.DynamicIntChoiceGenerator;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.access.Unknown;

public class JPF_gov_nasa_jpf_abstraction_Verifier extends NativePeer {
    @MJI
    public static int unknownInt____I(MJIEnv env, int clsObjRef) {
        ThreadInfo ti = env.getThreadInfo();

        if (!ti.isFirstStepInsn()) { // first time around
            IntChoiceGenerator cg = new DynamicIntChoiceGenerator("unknownInt()", new int[] { 0 }); // Fixed default value

            if (env.setNextChoiceGenerator(cg)) {
                env.repeatInvocation();
            }

            return 0; // Fixed Dummy Value
        } else {
            ChoiceGenerator<?> cg = env.getChoiceGenerator();

            assert (cg instanceof DynamicIntChoiceGenerator) : "expected DynamicIntChoiceGenerator, got: " + cg;

            DynamicIntChoiceGenerator icg = (DynamicIntChoiceGenerator) cg;

            Unknown unknown = Unknown.create(ti.getPC(), icg);

            PredicateAbstraction.getInstance().registerUnknown(unknown);

            env.setReturnAttribute(unknown);

            return icg.getNextChoice();
        }
    }
}
