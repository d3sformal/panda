package gov.nasa.jpf.abstraction.bytecode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Implementation of all binary IF instructions regardless their precise type.
 */
public class BinaryIfInstructionExecutor {

    final public Instruction execute(AbstractBranching br, ThreadInfo ti) {

        String name = br.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();
        Expression expr1 = ExpressionUtil.getExpression(sf.getOperandAttr(1));
        Expression expr2 = ExpressionUtil.getExpression(sf.getOperandAttr(0));

        boolean conditionValue;

        int v1 = sf.peek(1);
        int v2 = sf.peek(0);

        /**
         * First we check whether there is no choice generator present
         * If not we evaluate the branching condition
         * Otherwise we inspect all the choices
         */
        if (!ti.isFirstStepInsn()) { // first time around
            TruthValue truth = TruthValue.UNDEFINED;

            /**
             * If there is enough information (symbolic expressions) to decide the condition we ask abstractions to provide the truth value
             * Only predicate abstraction is designed to respond with a valid value (TRUE, FALSE, UNKNOWN).
             * No other abstraction can do that, the rest of them returns UNDEFINED.
             */
            if (expr1 != null && expr2 != null && RunDetector.isRunning()) {
                Predicate predicate = br.createPredicate(expr1, expr2);
                truth = PredicateAbstraction.getInstance().processBranchingCondition(br.getSelf().getPosition(), predicate);
            }

            switch (truth) {
                // IF THE BRANCH COULD NOT BE PICKED BY PREDICATE ABSTRACTION (IT IS NOT ACTIVE)
                default:
                case UNDEFINED:
                    return br.executeConcrete(ti);
                case TRUE:
                    ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
                    conditionValue = true;
                    break;
                case FALSE:
                    ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
                    conditionValue = false;
                    break;
                case UNKNOWN:
                    ChoiceGenerator<?> cg = new AbstractChoiceGenerator();
                    ss.setNextChoiceGenerator(cg);

                    return br.getSelf();
            }
        } else { // this is what really returns results
            ChoiceGenerator<?> cg = ss.getChoiceGenerator();

            assert (cg instanceof AbstractChoiceGenerator) : "expected AbstractChoiceGenerator, got: " + cg;

            conditionValue = (Integer) cg.getNextChoice() == 0 ? false : true;

            if (expr1 != null && expr2 != null) {
                Predicate predicate = br.createPredicate(expr1, expr2);
                PredicateAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.create(conditionValue)));
            }
        }

        sf.pop();
        sf.pop();

        IfHelper.synchronizeConcreteAndAbstractExecutions(br, ti, v1, v2, expr1, expr2, conditionValue);

        return (conditionValue ? br.getTarget() : br.getNext(ti));
    }
}
