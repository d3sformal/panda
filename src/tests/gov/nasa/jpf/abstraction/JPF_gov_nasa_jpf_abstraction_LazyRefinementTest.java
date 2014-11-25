package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;

public class JPF_gov_nasa_jpf_abstraction_LazyRefinementTest extends NativePeer {
    private static int choice;
    private static boolean refinement = false;

    @MJI
    public void createChoices____V(MJIEnv env, int clsObjRef) {
        ThreadInfo ti = env.getThreadInfo();

        if (!ti.isFirstStepInsn()) {
            IntChoiceFromList cg = new IntChoiceFromList("choices", 0, 1, 2, 3, 4, 5);

            if (env.setNextChoiceGenerator(cg)) {
                env.repeatInvocation();
            }
        } else {
            IntChoiceFromList cg = (IntChoiceFromList) env.getChoiceGenerator();

            choice = cg.getNextChoice();
        }
    }

    @MJI
    public void error____V(MJIEnv env, int clsObjRef) {
        if (choice == 4 && !refinement) {
            Predicate p1 = Equals.create(DefaultRoot.create("x"), Constant.create(0));
            Predicate p2 = LessThan.create(DefaultRoot.create("x"), Constant.create(0));

            PredicateAbstraction.getInstance().getTraceFormula().markCallInvoked();
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(p1, env.getMethodInfo(), 0);
            PredicateAbstraction.getInstance().getTraceFormula().markCallStarted();
            PredicateAbstraction.getInstance().extendTraceFormulaWithConstraint(p2, env.getMethodInfo(), 1);
            PredicateAbstraction.getInstance().getTraceFormula().markReturn();
            PredicateAbstraction.getInstance().getTraceFormula().markReturned();

            env.throwException("java.lang.RuntimeException");

            refinement = true;
        }
    }
}
