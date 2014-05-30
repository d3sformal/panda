package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;

/**
 * Implementation of all binary IF instructions regardless their precise type.
 */
public class BinaryIfInstructionExecutor {

    final public Instruction execute(AbstractBranching br, ThreadInfo ti) {

        String name = br.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();
        Attribute attr1 = Attribute.getAttribute(sf.getOperandAttr(1));
        Attribute attr2 = Attribute.getAttribute(sf.getOperandAttr(0));

        AbstractValue abs_v1 = Attribute.getAbstractValue(attr1);
        AbstractValue abs_v2 = Attribute.getAbstractValue(attr2);
        Expression expr1 = Attribute.getExpression(attr1);
        Expression expr2 = Attribute.getExpression(attr2);

        AbstractBoolean abs_condition = null;

        boolean conditionValue;

        int v1 = sf.peek(1);
        int v2 = sf.peek(0);

        /**
         * First we check whether there is no choice generator present
         * If not we evaluate the branching condition
         * Otherwise we inspect all the choices
         */
        if (!ti.isFirstStepInsn()) { // first time around
            /**
             * If there is enough information (symbolic expressions) to decide the condition we ask abstractions to provide the truth value
             * Only predicate abstraction is designed to respond with a valid value (TRUE, FALSE, UNKNOWN).
             * No other abstraction can do that, the rest of them returns UNDEFINED.
             */
            if (expr1 != null && expr2 != null && RunDetector.isRunning()) {
                Predicate predicate = br.createPredicate(expr1, expr2);
                TruthValue truth = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(predicate);

                switch (truth) {
                case TRUE:
                    abs_condition = AbstractBoolean.TRUE;
                    break;
                case FALSE:
                    abs_condition = AbstractBoolean.FALSE;
                    break;
                case UNKNOWN:
                    abs_condition = AbstractBoolean.TOP;
                    break;
                }
            }

            // IF THE abs_condition COULD NOT BE DERIVED BY PREDICATE ABSTRACTION (IT IS NOT ACTIVE)
            if (abs_condition == null) {
                if (abs_v1 == null && abs_v2 == null) { // the condition is concrete
                    return br.executeConcrete(ti);
                }

                // the condition is abstract

                // NUMERIC ABSTRACTION
                abs_condition = br.getCondition(v1, abs_v1, v2, abs_v2);
            }

            if (abs_condition == AbstractBoolean.TRUE) {
                ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
                conditionValue = true;
            } else if (abs_condition == AbstractBoolean.FALSE) {
                ti.breakTransition("Ensure that state matching is used in case there was an infinite loop");
                conditionValue = false;
            } else { // TOP
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
                GlobalAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.create(conditionValue)));
            }
        }

        sf.pop();
        sf.pop();

        if (br.getConcreteBranchValue(v1, v2) != TruthValue.create(conditionValue)) {
            System.err.println("[WARNING] Inconsistent concrete and abstract branching: " + br.createPredicate(expr1, expr2));

            if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.pruning")) {
                ss.setIgnored(true);
            } else if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.adjusting_concrete_values")) {
                Map<AccessExpression, Integer> consistentValues = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getPredicateValuation().get(0).getConcreteState();

                for (AccessExpression expr : consistentValues.keySet()) {
                    injectConcreteValueIntoJPFState(expr, consistentValues.get(expr), ti, sf);
                }
            }
        }

        return (conditionValue ? br.getTarget() : br.getNext(ti));
    }

    private void injectConcreteValueIntoJPFState(AccessExpression expr, int value, ThreadInfo ti, StackFrame sf) {
        if (expr.getRoot() instanceof AnonymousExpression) {
            // ...
        } else if (expr.isStatic()) {
            Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

            ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0).lookupValues(expr.getRoot(), values);

            assert values.size() == 1;
            // ...
        } else {
            if (expr.isLocalVariable()) {
                LocalVarInfo lvi = sf.getLocalVarInfo(expr.getRoot().getName());

                // Update only variables that are in scope
                if (lvi != null) {
                    sf.setLocalVariable(lvi.getSlotIndex(), value);
                }
            } else {
                Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

                ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0).lookupValues(expr.getRoot(), values);

                assert values.size() == 1;

                Reference r = (Reference) values.iterator().next();

                ElementInfo ei = r.getElementInfo();

                injectConcreteValueIntoJPFElement(ei, expr, 2, value, ti);
            }
        }
    }

    private void injectConcreteValueIntoJPFElement(ElementInfo ei, AccessExpression expr, int i, int value, ThreadInfo ti) {
        if (i == expr.getLength()) {
            if (expr.get(i) instanceof ObjectFieldRead) {
                ObjectFieldRead r = (ObjectFieldRead) expr.get(i);

                ei.setIntField(r.getField().getName(), value);
            } else {
                ArrayElementRead r = (ArrayElementRead) expr.get(i);

                //Exact value of the index? (may have changed)
                //ei.getArrayFields().setIntValue(r.getIndex(), value);
            }
        } else {
            ElementInfo subEI = null;

            if (expr.get(i) instanceof ObjectFieldRead) {
                ObjectFieldRead r = (ObjectFieldRead) expr.get(i);

                subEI = ti.getElementInfo(ei.getReferenceField(r.getField().getName()));
            } else {
                ArrayElementRead r = (ArrayElementRead) expr.get(i);

                //Exact value of the index? (may have changed)
                //subEI = ti.getElementInfo(ei.getArrayFields().getReferenceValue(r.getIndex()));
            }

            injectConcreteValueIntoJPFElement(subEI, expr, i + 1, value, ti);
        }
    }
}
