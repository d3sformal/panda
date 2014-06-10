package gov.nasa.jpf.abstraction.bytecode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractChoiceGenerator;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.predicate.state.universe.ClassName;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

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
                truth = PredicateAbstraction.getInstance().processBranchingCondition(predicate);
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

        if (br.getConcreteBranchValue(v1, v2) != TruthValue.create(conditionValue)) {
            System.err.println("[WARNING] Inconsistent concrete and abstract branching: " + br.createPredicate(expr1, expr2));

            if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.pruning")) {
                ss.setIgnored(true);
            } else if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.adjusting_concrete_values")) {
                Map<AccessExpression, ElementInfo> primitiveExprs = new HashMap<AccessExpression, ElementInfo>();
                Set<AccessExpression> allExprs = new HashSet<AccessExpression>();

                PredicateAbstraction.getInstance().getPredicateValuation().get(0).addAccessExpressionsToSet(allExprs);
                collectAllStateExpressions(primitiveExprs, allExprs, sf, ti);

                ElementInfo[] targetArray = new ElementInfo[primitiveExprs.keySet().size()];
                AccessExpression[] exprArray = new AccessExpression[primitiveExprs.keySet().size()];

                int i = 0;
                for (AccessExpression expr : primitiveExprs.keySet()) {
                    exprArray[i] = expr;
                    targetArray[i] = primitiveExprs.get(expr);
                    ++i;
                }

                int[] valueArray = PredicateAbstraction.getInstance().getPredicateValuation().get(0).getConcreteState(exprArray);

                for (int j = 0; j < exprArray.length; ++j) {
                    injectConcreteValueIntoJPFState(exprArray[j], valueArray[j], targetArray[j], sf, ti);
                }
            }
        }

        return (conditionValue ? br.getTarget() : br.getNext(ti));
    }

    // Collects all deterministic (no non-constant array index) access expressions that point to primitive data contributing to the current concrete state
    // The set of access expressions is restricted to the current scope
    private void collectAllStateExpressions(Map<AccessExpression, ElementInfo> stateExprs, Set<AccessExpression> allExprs, StackFrame sf, ThreadInfo ti) {
        Set<UniverseIdentifier> cls = new HashSet<UniverseIdentifier>();

        for (AccessExpression expr : allExprs) {
            Root root = expr.getRoot();

            if (root.isLocalVariable()) {
                int idx = sf.getLocalVariableSlotIndex(root.getName());

                if (idx >= 0) {
                    if (sf.isLocalVariableRef(idx)) {
                        collectStateExpressions(stateExprs, ti, ti.getElementInfo(sf.getLocalVariable(idx)), expr, 2, root);
                    } else {
                        stateExprs.put(root, null);
                    }
                }
            } else if (root.isStatic()) {
                cls.clear();

                PredicateAbstraction.getInstance().getSymbolTable().get(0).lookupValues(root, cls);

                assert cls.size() == 1;

                ClassName clsName = (ClassName) cls.iterator().next();

                collectStateExpressions(stateExprs, ti, clsName.getStaticElementInfo(), expr, 2, root);
            }
        }
    }

    private void collectStateExpressions(Map<AccessExpression, ElementInfo> stateExprs, ThreadInfo ti, ElementInfo parent, AccessExpression expr, int i, AccessExpression prefix) {
        if (i < expr.getLength()) {
            AccessExpression access = expr.get(i);

            if (access instanceof ObjectFieldRead) {
                ObjectFieldRead r = (ObjectFieldRead) access;

                if (parent.getClassInfo().getInstanceField(r.getField().getName()).isReference()) {
                    collectStateExpressions(stateExprs, ti, ti.getElementInfo(parent.getReferenceField(r.getField().getName())), expr, i + 1, r.reRoot(prefix));
                } else {
                    stateExprs.put(r.reRoot(prefix), parent);
                }
            } else if (access instanceof ArrayElementRead) {
                ArrayElementRead r = (ArrayElementRead) access;
                int[] indices;

                if (r.getIndex() instanceof Constant) {
                    indices = new int[] {((Constant) r.getIndex()).value.intValue()};
                } else {
                    indices = PredicateAbstraction.getInstance().computeAllExpressionValuesInRange(r.getIndex(), 0, parent.arrayLength());
                }

                for (int index : indices) {
                    if (parent.isReferenceArray()) {
                        collectStateExpressions(stateExprs, ti, ti.getElementInfo(parent.getArrayFields().getReferenceValue(index)), expr, i + 1, DefaultArrayElementRead.create(prefix, Constant.create(index)));
                    } else {
                        stateExprs.put(DefaultArrayElementRead.create(prefix, Constant.create(index)), parent);
                    }
                }
            }
        }
    }

    private void injectConcreteValueIntoJPFState(AccessExpression expr, int value, ElementInfo ei, StackFrame sf, ThreadInfo ti) {
        if (ei == null) {
            LocalVarInfo lvi = sf.getLocalVarInfo(expr.getRoot().getName());

            // Update only variables that are in scope
            if (lvi != null) {
                sf.setLocalVariable(lvi.getSlotIndex(), value);
            }
        } else if (expr instanceof ObjectFieldRead) {
            ObjectFieldRead r = (ObjectFieldRead) expr;

            ti.getModifiableElementInfo(ei.getObjectRef()).setIntField(r.getField().getName(), value);
        } else if (expr instanceof ArrayElementRead) {
            ArrayElementRead r = (ArrayElementRead) expr;
            Constant c = (Constant) r.getIndex();

            ti.getModifiableElementInfo(ei.getObjectRef()).getArrayFields().setIntValue(c.value.intValue(), value);
        } else {
            throw new RuntimeException("Cannot inject value into anything else than local variable, object field, static field and array element.");
        }
    }
}
