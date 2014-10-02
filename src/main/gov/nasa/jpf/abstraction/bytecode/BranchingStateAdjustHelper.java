package gov.nasa.jpf.abstraction.bytecode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.DynamicIntChoiceGenerator;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.Unknown;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;

public class BranchingStateAdjustHelper {
    public static void synchronizeConcreteAndAbstractExecutions(AbstractBranching br, ThreadInfo ti, int v1, int v2, Expression expr1, Expression expr2, boolean abstractJump) {
        StackFrame sf = ti.getModifiableTopFrame();
        SystemState ss = ti.getVM().getSystemState();

        // In case the concrete execution does not allow the same branch to be taken
        if (br.getConcreteBranchValue(v1, v2) != TruthValue.create(abstractJump)) {
            Predicate branchCondition = br.createPredicate(expr1, expr2);

            if (!abstractJump) {
                branchCondition = Negation.create(branchCondition);
            }

            System.err.println("[WARNING] Inconsistent concrete and abstract branching: " + branchCondition);

            // Either cut of the inconsistent branch
            // or make the concrete state represent the abstract one (force concrete values)
            if (ti.getVM().getJPF().getConfig().getBoolean("panda.branch.prune_infeasible")) {
                ss.setIgnored(true);

                if (ti.getVM().getJPF().getConfig().getBoolean("panda.branch.force_feasible")) {
                    System.out.println("[WARNING] Finding feasible trace with matching prefix and " + branchCondition);

                    Predicate traceFormula = PredicateAbstraction.getInstance().getTraceFormula().toConjunction();
                    Map<String, Unknown> unknowns = PredicateAbstraction.getInstance().getUnknowns();

                    int i = 0;
                    AccessExpression[] exprArray = new AccessExpression[unknowns.keySet().size()];

                    // Collect unknown expressions
                    for (String unknown : unknowns.keySet()) {
                        exprArray[i] = DefaultRoot.create(unknown);

                        ++i;
                    }

                    // Add blocking clause for unknown models
                    //   (u1 != v11 & u1 != v12 & ... u1 != v1n) | (u2 != ...) | ... (un != ...)
                    Predicate blockings = Contradiction.create();

                    for (int j = 0; j < exprArray.length; ++j) {
                        Predicate blocking = Tautology.create();

                        for (int model : unknowns.get(((DefaultRoot) exprArray[j]).getName()).getChoiceGenerator().getChoices()) {
                            blocking = Conjunction.create(blocking, Negation.create(Equals.create(exprArray[j], Constant.create(model))));
                        }

                        blockings = Disjunction.create(blockings, blocking);
                    }

                    traceFormula = Conjunction.create(traceFormula, blockings);

                    int[] models = PredicateAbstraction.getInstance().getPredicateValuation().get(0).getModels(traceFormula, exprArray);

                    if (models == null) {
                        System.out.println("[WARNING] No feasible trace found");
                    } else {
                        // TODO: PROBLEM!!! :) The trace found with this choices will force finding models for the original branch (now enabled, then disabled)
                        System.out.println("[WARNING] Feasible trace found for unknown values: " + Arrays.toString(models));
                        for (int j = 0; j < models.length; ++j) {
                            DynamicIntChoiceGenerator cg = unknowns.get(((DefaultRoot) exprArray[j]).getName()).getChoiceGenerator();

                            if (cg.hasProcessed(models[j]) || !cg.has(models[j])) {
                                cg.add(models[j]);
                            }
                        }
                    }
                }
            } else if (ti.getVM().getJPF().getConfig().getBoolean("panda.branch.adjust_concrete_values")) {
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
                int[] valueArray = PredicateAbstraction.getInstance().getPredicateValuation().get(0).getConcreteState(exprArray, br.getSelf().getPosition());

                if (valueArray == null) {
                    throw new RuntimeException("Cannot compute the corresponding concrete state.");
                }

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
