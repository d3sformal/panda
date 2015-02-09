package gov.nasa.jpf.abstraction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ByteArrayFields;
import gov.nasa.jpf.vm.CharArrayFields;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.DoubleArrayFields;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.FloatArrayFields;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.IntArrayFields;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.LongArrayFields;
import gov.nasa.jpf.vm.ShortArrayFields;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.StateSet;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

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
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.Unknown;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;

public class BranchingExecutionHelper {
    public static Instruction synchronizeConcreteAndAbstractExecutions(ThreadInfo ti, Predicate branchCondition, boolean concreteJump, boolean abstractJump, Instruction target, Instruction self) {
        StackFrame sf = ti.getModifiableTopFrame();
        SystemState ss = ti.getVM().getSystemState();

        PandaConfig config = PandaConfig.getInstance();

        boolean pruneInfeasible = config.pruneInfeasibleBranches();
        boolean forceFeasibleOnce = config.forceFeasibleBranchesOnce();
        boolean forceFeasible = forceFeasibleOnce || config.forceFeasibleBranches();

        if (concreteJump == abstractJump) { // In case concrete and abstract executions agree
            if (pruneInfeasible && forceFeasibleOnce) {
                // Add state to allow forward state matching (look if we should generate more choices to get into the branch (it might have been explored before - actually it is explored when this branch is hit))
                if (ti.isFirstStepInsn()) {
                    ss.setForced(true);
                }
                ti.breakTransition("Creating state after taking an enabled branch: " + branchCondition);

                PredicateAbstraction pabs = PredicateAbstraction.getInstance();
                TraceFormula tf = pabs.getTraceFormula();
                TraceFormula ft = pabs.getForcedTraceFormula();

                if (ft != null) {
                    if (!tf.isPrefixOf(ft) || tf.size() >= ft.size()) {
                        pabs.dropForceStatesAlongTrace();
                    }
                }

                return self;
            }
        } else { // In case the concrete execution does not allow the same branch to be taken
            if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                System.err.println("[WARNING] Inconsistent concrete and abstract branching: " + branchCondition);
            }

            // Either cut of the inconsistent branch
            // or make the concrete state represent the abstract one (force concrete values)
            if (pruneInfeasible) {
                PredicateAbstraction.getInstance().dropForceStatesAlongTrace();
                ti.breakTransition("Ignore inconsistent state");
                ss.setForced(false);
                ss.setIgnored(true);

                if (forceFeasible) {
                    if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                        System.out.println("[WARNING] Deciding feasibility of the following trace (prefix matching current trace + " + branchCondition + ")");

                        for (Step s : PredicateAbstraction.getInstance().getTraceFormula()) {
                            System.out.println("\t" + s.getPredicate());
                        }
                    }

                    Predicate traceFormula = PredicateAbstraction.getInstance().getTraceFormula().toConjunction();
                    Map<String, Unknown> allUnknowns = PredicateAbstraction.getInstance().getUnknowns();
                    Map<String, Unknown> unknowns = new HashMap<String, Unknown>();

                    int i = 0;

                    Set<AccessExpression> exprs = new HashSet<AccessExpression>();
                    Set<AccessExpression> unknownExprs = new HashSet<AccessExpression>();

                    traceFormula.addAccessExpressionsToSet(exprs);

                    // Collect unknown expressions
                    // Filter out only those that appear along the trace
                    for (String unknown : allUnknowns.keySet()) {
                        AccessExpression ae = DefaultRoot.create(unknown);

                        if (exprs.contains(ae)) {
                            unknowns.put(unknown, allUnknowns.get(unknown));
                            unknownExprs.add(ae);
                        }

                        ++i;
                    }

                    AccessExpression[] exprArray = unknownExprs.toArray(new AccessExpression[unknownExprs.size()]);

                    /**
                     * Blocking clauses may not be necessary (the trace itself (extended with appropriate branch condition) is enough to demand a new value of unknown)
                     * On contrary they may cause divergence
                     * But they are there currently to avoid returning to previously picked choices:
                     *   i = *
                     *   if (i = 1) {
                     *   }
                     *
                     *   Starts with 0
                     *   Generates 1
                     *   Goes back to 0
                     *   Goes back to 1
                     *   Goes back to 0
                     *   ...
                     */
                    // Add blocking clause for unknown models
                    //   (u1 != v11 & u1 != v12 & ... u1 != v1n) | (u2 != ...) | ... (un != ...)

                    Predicate reuses = Tautology.create();
                    Predicate blockings = Contradiction.create();

                    for (int j = 0; j < exprArray.length; ++j) {
                        Predicate reuse = Contradiction.create();
                        Predicate blocking = Tautology.create();
                        DynamicIntChoiceGenerator cg = unknowns.get(((DefaultRoot) exprArray[j]).getName()).getChoiceGenerator();
                        Integer[] choices = cg.getProcessedChoices();
                        List<TraceFormula> traces = cg.getTraces();

                        for (int k = 0; k < choices.length; ++k) {
                            int model = choices[k];
                            Predicate binding = Equals.create(exprArray[j], Constant.create(model));

                            // Block only those models that were created for the same trace (other choices applied in other branches of the state space may easily be reused here)
                            // Actually it would be wrong to omit models that might easily enable this branch but happen to be used elsewhere first (That would destroy soundness)
                            if (k == 0 || traces.get(k - 1).equals(PredicateAbstraction.getInstance().getTraceFormula())) {
                                blocking = Conjunction.create(blocking, Negation.create(binding));
                            }

                            // In contrast to blocking:
                            // Force using one of the old models of the given unknown
                            reuse = Disjunction.create(reuse, binding);
                        }

                        reuses = Conjunction.create(reuses, reuse);
                        blockings = Disjunction.create(blockings, blocking);
                    }

                    Predicate oldModelFormula = Conjunction.create(traceFormula, reuses);
                    Predicate newModelFormula = Conjunction.create(traceFormula, blockings);

                    // First, try using a combination of old (already generated) models for the unknowns
                    int[] models = PredicateAbstraction.getInstance().getPredicateValuation().get(0).getModels(oldModelFormula, exprArray);

                    // Only if none exists, generate a possibly completely different model (may change all the values to something new, if used unwisely may cause divergence - too many different model combinations)
                    if (models == null) {
                        models = PredicateAbstraction.getInstance().getPredicateValuation().get(0).getModels(newModelFormula, exprArray);
                    }

                    if (models == null || models.length == 0) {
                        if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                            System.out.println("[WARNING] No feasible trace found");
                        }
                    } else {
                        // To avoid divergence:
                        //   one concrete path allows only one branch -> second trace is explored to cover the other branch
                        //   the second concrete path does not allow the first branch -> third trace is explored to cover the first branch
                        //   ...
                        //
                        // Assume this is the case:
                        //   We revisit this branch (the other branch has already been visited - it created a state after passing the check)
                        // Then:
                        //   Try to create a state (but not store it) if the state matches then dont add the choices
                        //     - We cannot store it now because:
                        //       - If this is the first visit -> first enable of a disabled branch -> we WOULD CREATE STATE in the branch -> when we get here with the correct concrete model we would match and not continue exploring
                        //

                        StateSet stateSet = VM.getVM().getStateSet();
                        ResetableStateSet rStateSet = null;

                        if (stateSet instanceof ResetableStateSet) {
                            rStateSet = (ResetableStateSet) stateSet;
                        }

                        // Currently not true:
                        //   Because we want to match current state (its working copy) with state created by the code in enabled branch
                        //   Here we have not yet advanced PC
                        //   Whereas in the enabled branch the state is made after advancing the PC
                        //   We need to advance the PC to get the correct state
                        //   ti.setPC(target);

                        if (rStateSet == null || rStateSet.isCurrentUnique() || !forceFeasibleOnce) {
                            if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                                System.out.println("[WARNING] Feasible trace found for unknown values: " + Arrays.toString(models));
                            }

                            boolean isNewCombination = false;

                            for (int j = 0; j < models.length; ++j) {
                                DynamicIntChoiceGenerator cg = unknowns.get(((DefaultRoot) exprArray[j]).getName()).getChoiceGenerator();

                                if (!cg.has(models[j])) {
                                    isNewCombination = true;
                                }
                            }

                            for (int j = 0; j < models.length; ++j) {
                                DynamicIntChoiceGenerator cg = unknowns.get(((DefaultRoot) exprArray[j]).getName()).getChoiceGenerator();

                                if ((cg.hasProcessed(models[j]) && isNewCombination) || !cg.has(models[j])) {
                                    cg.add(models[j]);

                                    if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                                        System.out.println("\t" + exprArray[j] + ": " + models[j]);
                                    }
                                }
                            }
                        }
                    }
                }

                return self;
            } else if (config.adjustConcreteValues()) {
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
                int[] valueArray = PredicateAbstraction.getInstance().getPredicateValuation().get(0).getConcreteState(exprArray, self.getPosition());

                if (valueArray == null) {
                    if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                        System.out.println("[WARNING] Cannot compute the corresponding concrete state.");
                    }

                    // It is possible to reach an inconsistent state
                    //
                    // Be in method m1 with some fact1
                    // Enter method m2 (without carrying fact1 over) and assume fact2 at some non-deterministic branching during its execution
                    // Return back to m1, use knowledge of fact1 (caller), fact2 (callee) to derive inconsistency and pollute the abstract state with it
                    // Arrive at another non-det branching where now both branches are equally possible (inconsistent state enables anything)
                    // Attempt to derive model for an inconsistent state (done by the concrete-value-adjusting procedure)
                    // Fail (No model for an UNSAT formula)
                    //
                    // That is why we abandon the trace
                    ti.breakTransition("Ignore inconsistent state");
                    ss.setForced(false);
                    ss.setIgnored(true);

                    return self;
                } else {
                    // Inject the newly computed values into the concrete state
                    for (int j = 0; j < exprArray.length; ++j) {
                        adjustValueInConcreteState(exprArray[j], valueArray[j], targetArray[j], sf, ti);
                    }
                }
            }
        }

        return target;
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

                if (cls.size() != 1) {
                    throw new RuntimeException("Ambiguity when searching for class `" + root + "`");
                }

                ClassName clsName = (ClassName) cls.iterator().next();

                collectStateExpressions(stateExprs, ti, clsName.getStaticElementInfo().getClassInfo().getStaticElementInfo(), expr, 2, root);
            }
        }
    }

    // Recursively expands expressions
    // Used in collectAllStateExpressions only
    private static void collectStateExpressions(Map<AccessExpression, ElementInfo> stateExprs, ThreadInfo ti, ElementInfo parent, AccessExpression expr, int i, AccessExpression prefix) {
        if (parent == null) {
            if (PandaConfig.getInstance().enabledVerbose(BranchingExecutionHelper.class)) {
                System.out.println("Attempting to collect subexpressions of null (" + expr + ")");
            }

            return;
        }

        if (i <= expr.getLength()) {
            AccessExpression access = expr.get(i);

            if (access instanceof ObjectFieldRead) {
                ObjectFieldRead r = (ObjectFieldRead) access;

                FieldInfo fi;

                if (parent instanceof StaticElementInfo) {
                    fi = parent.getClassInfo().getStaticField(r.getField().getName());
                } else {
                    fi = parent.getClassInfo().getInstanceField(r.getField().getName());
                }

                if (fi.isReference()) {
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
                    if (index < parent.arrayLength()) {
                        if (parent.isReferenceArray()) {
                            collectStateExpressions(stateExprs, ti, ti.getElementInfo(parent.getArrayFields().getReferenceValue(index)), expr, i + 1, DefaultArrayElementRead.create(prefix, Constant.create(index)));
                        } else {
                            stateExprs.put(DefaultArrayElementRead.create(prefix, Constant.create(index)), parent);
                        }
                    }
                }
            }
        }
    }

    private static void adjustValueInConcreteState(AccessExpression expr, int value, ElementInfo ei, StackFrame sf, ThreadInfo ti) {
        PandaConfig config = PandaConfig.getInstance();

        if (ei == null) {
            LocalVarInfo lvi = sf.getLocalVarInfo(expr.getRoot().getName());

            // Update only variables that are in scope
            if (lvi != null && !sf.isLocalVariableRef(lvi.getSlotIndex())) {
                sf.setLocalVariable(lvi.getSlotIndex(), value);

                if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                    System.out.println("Setting local variable slot #" + lvi.getSlotIndex() + " (" + expr + ") to " + value + " " + lvi);
                }
            }
        } else if (expr instanceof ObjectFieldRead) {
            ObjectFieldRead r = (ObjectFieldRead) expr;
            FieldInfo fi;

            if (ei instanceof StaticElementInfo) {
                fi = ei.getClassInfo().getStaticField(r.getField().getName());
            } else {
                fi = ei.getClassInfo().getInstanceField(r.getField().getName());
            }

            ei = ei.getModifiableInstance();

            if (fi.is1SlotField()) {
                ei.set1SlotField(fi, value);
            } else {
                ei.set2SlotField(fi, (long)value);
            }

            if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                System.out.println("Setting object field `" + r.getField().getName() + "` of " + ei.getObjectRef() + " (" + expr + ") to " + value);
            }
        } else if (expr instanceof ArrayElementRead) {
            ArrayElementRead r = (ArrayElementRead) expr;
            Constant c = (Constant) r.getIndex();

            ei = ei.getModifiableInstance();

            ArrayFields af = ei.getArrayFields();

            if (af instanceof IntArrayFields) {
                af.setIntValue(c.value.intValue(), value);
            } else if (af instanceof FloatArrayFields) {
                af.setFloatValue(c.value.intValue(), (float) value);
            } else if (af instanceof LongArrayFields) {
                af.setLongValue(c.value.intValue(), (long) value);
            } else if (af instanceof DoubleArrayFields) {
                af.setDoubleValue(c.value.intValue(), (double) value);
            } else if (af instanceof ByteArrayFields) {
                af.setByteValue(c.value.intValue(), (byte) value);
            } else if (af instanceof CharArrayFields) {
                af.setCharValue(c.value.intValue(), (char) value);
            } else if (af instanceof ShortArrayFields) {
                af.setShortValue(c.value.intValue(), (short) value);
            }

            if (config.enabledVerbose(BranchingExecutionHelper.class)) {
                System.out.println("Setting array element number " + c.value.intValue() + " of " + ei.getObjectRef() + " (" + expr + ") to " + value);
            }
        } else {
            throw new RuntimeException("Cannot inject value into anything else than local variable, object field, static field and array element.");
        }
    }
}
