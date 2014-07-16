package gov.nasa.jpf.abstraction.bytecode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;

public class IfHelper {
    public static void synchronizeConcreteAndAbstractExecutions(AbstractBranching br, ThreadInfo ti, int v1, int v2, Expression expr1, Expression expr2, boolean abstractJump) {
        StackFrame sf = ti.getModifiableTopFrame();
        SystemState ss = ti.getVM().getSystemState();

        // In case the concrete execution does not allow the same branch to be taken
        if (br.getConcreteBranchValue(v1, v2) != TruthValue.create(abstractJump)) {
            System.err.println("[WARNING] Inconsistent concrete and abstract branching: " + br.createPredicate(expr1, expr2));

            // Either cut of the inconsistent branch
            // or make the concrete state represent the abstract one (force concrete values)
            if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.prune_infeasible")) {
                ss.setIgnored(true);
            } else if (ti.getVM().getJPF().getConfig().getBoolean("apf.branch.adjust_concrete_values")) {
                Map<AccessExpression, ElementInfo> primitiveExprs = new HashMap<AccessExpression, ElementInfo>();
                Set<AccessExpression> allExprs = new HashSet<AccessExpression>();

                PredicateAbstraction.getInstance().getPredicateValuation().get(0).addAccessExpressionsToSet(allExprs);

                // Collect all access expressions pointing at primitive values
                // Restrain to those that are mentioned in predicates
                // These will be tweaked so that they represent the abstract state
                collectAllStateExpressions(primitiveExprs, allExprs, sf, ti);

                ElementInfo[] targetArray = new ElementInfo[primitiveExprs.keySet().size()];
                AccessExpression[] exprArray = new AccessExpression[primitiveExprs.keySet().size()];

                int i = 0;
                for (AccessExpression expr : primitiveExprs.keySet()) {
                    exprArray[i] = expr;
                    targetArray[i] = primitiveExprs.get(expr);
                    ++i;
                }

                // Compute a concrete (sub)state representing the abstract one
                int[] valueArray = PredicateAbstraction.getInstance().getPredicateValuation().get(0).getConcreteState(exprArray);

                // Inject the newly computed values into the concrete state
                for (int j = 0; j < exprArray.length; ++j) {
                    adjustValueInConcreteState(exprArray[j], valueArray[j], targetArray[j], sf, ti);
                }
            }
        }
    }

    // Collects all deterministic (only constant array indices) access expressions that point to primitive data contributing to the current concrete state
    //   State expression ~ access expression pointing at a primitive value that contributes to the concrete state
    // The set of access expressions is restricted to the current scope
    // Effectively it converts all expressions in `allExprs` of the form `a[expr]` into `a[0]` ... `a[n]`
    private static void collectAllStateExpressions(Map<AccessExpression, ElementInfo> stateExprs, Set<AccessExpression> allExprs, StackFrame sf, ThreadInfo ti) {
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

    // Recursively expands expressions
    // Used in collectAllStateExpressions only
    private static void collectStateExpressions(Map<AccessExpression, ElementInfo> stateExprs, ThreadInfo ti, ElementInfo parent, AccessExpression expr, int i, AccessExpression prefix) {
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

    private static void adjustValueInConcreteState(AccessExpression expr, int value, ElementInfo ei, StackFrame sf, ThreadInfo ti) {
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
