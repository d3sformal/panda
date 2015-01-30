package gov.nasa.jpf.abstraction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.Path;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.BranchingCondition;
import gov.nasa.jpf.abstraction.common.BranchingConditionInfo;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.BranchingDecision;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.SpecialVariable;
import gov.nasa.jpf.abstraction.common.access.Unknown;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.heuristic.RefinementHeuristic;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.state.MethodFramePredicateValuation;
import gov.nasa.jpf.abstraction.state.PredicateValuationStack;
import gov.nasa.jpf.abstraction.state.State;
import gov.nasa.jpf.abstraction.state.SymbolTableStack;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;
import gov.nasa.jpf.abstraction.state.SystemSymbolTable;
import gov.nasa.jpf.abstraction.state.Trace;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.ElementIndex;
import gov.nasa.jpf.abstraction.state.universe.FieldName;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.StructuredValue;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueSlot;
import gov.nasa.jpf.abstraction.state.universe.UniverseArray;
import gov.nasa.jpf.abstraction.state.universe.UniverseObject;
import gov.nasa.jpf.abstraction.state.universe.UniverseSlotKey;
import gov.nasa.jpf.abstraction.util.CounterexampleListener;
import gov.nasa.jpf.abstraction.util.Pair;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Predicate abstraction class
 *
 * Predicate abstraction is defined by a global
 * (in respect to individual runtime elements (objects))
 * container of all predicate valuations
 *
 * Apart from that it uses an auxiliary symbol table -
 * a structure responsible for identification of aliases between
 * different access expressions (used in predicates or updated by instructions)
 */
public class PredicateAbstraction extends Abstraction {
    private SystemSymbolTable symbolTable;
    private SystemPredicateValuation predicateValuation;
    private Trace trace;
    private TraceFormula traceFormula;
    private TraceFormula forcedTrace;

    private boolean isInitialized = false;
    private Set<ClassInfo> startupClasses = new HashSet<ClassInfo>();

    /**
     * Methods visited along the currently explored trace
     *
     * Refinement of these methods forces the procedure to backtrack and recompute the part of the state space
     */
    private Set<MethodInfo> visitedMethods = new HashSet<MethodInfo>();

    public SMT smt = new SMT();
    private Stack<Pair<MethodInfo, Integer>> traceProgramLocations = new Stack<Pair<MethodInfo, Integer>>();
    private Predicates predicateSet;
    private int refinements = 0;
    private RefinementHeuristic heuristic;
    private ChoiceHistory history;

    private static PredicateAbstraction instance = null;
    private static List<CounterexampleListener> listeners = new ArrayList<CounterexampleListener>();
    private static Set<String> ignoredStaticFields = new HashSet<String>();

    static {
        ignoredStaticFields.add("serialVersionUID");
    }

    public static void setInstance(PredicateAbstraction instance) {
        PredicateAbstraction.instance = instance;
    }

    public static PredicateAbstraction getInstance() {
        return instance;
    }

    public static void registerCounterexampleListener(CounterexampleListener listener) {
        listeners.add(listener);
    }

    public static void unregisterListeners() {
        listeners.clear();
    }

    public PredicateAbstraction(Predicates predicateSet) {
        PandaConfig.reset();
        SMT.reset();

        symbolTable = new SystemSymbolTable(this);
        predicateValuation = new SystemPredicateValuation(this, predicateSet);
        heuristic = PandaConfig.getInstance().refinementHeuristic(predicateValuation);
        trace = new Trace();
        traceFormula = new TraceFormula();

        if (PandaConfig.getInstance().keepExploredBranches()) {
            history = new ChoiceHistory();
        }

        this.predicateSet = predicateSet;
    }

    public void rememberChoiceGenerator(ChoiceGenerator<?> cg) {
        if (PandaConfig.getInstance().keepExploredBranches()) {
            history.rememberChoiceGenerator(cg);
        }
    }

    public void rememberChoice(ChoiceGenerator<?> cg) {
        if (PandaConfig.getInstance().keepExploredBranches()) {
            history.rememberChoice();
        }
    }

    public void forgetChoiceGenerator() {
        if (PandaConfig.getInstance().keepExploredBranches()) {
            history.forgetChoiceGenerator();
        }
    }

    private SSAFormulaIncarnationsManager ssa = new SSAFormulaIncarnationsManager();
    private SortedMap<String, Unknown> unknowns = new TreeMap<String, Unknown>();

    public SSAFormulaIncarnationsManager getSSAManager() {
        return ssa;
    }

    public void registerUnknown(Unknown unknown) {
        unknowns.put(((Root) ssa.getSymbolIncarnation(unknown, 0)).getName(), unknown);
    }

    public SortedMap<String, Unknown> getUnknowns() {
        return unknowns;
    }

    private void extendTraceFormulaWithAssignment(AccessExpression to, Expression from, MethodInfo m, int nextPC, int depthDelta) {
        extendTraceFormulaWith(getTraceFormulaAssignmentConjunct(to, from, depthDelta), m, nextPC, false);
    }

    private Predicate getTraceFormulaAssignmentConjunct(AccessExpression to, Expression from, int depthDelta) {
        // The trace formula is extended with a constraint relating a POST STATE to a PRE STATE in the following fashion (for variables, field writes, array element writes):
        //
        // Variables:
        // a := <expr>  (Statement)
        // a' = ssa(<expr>)  (Constraint)
        //
        // Field writes:
        // a.f := <expr>
        // f' = fwrite(f, a, ssa(<expr>))
        //
        // Array element writes:
        // a[i] := <expr>
        // arr' = awrite(arr, a, i, ssa(<expr>))
        //
        // where `ssa( ... )` denotes expression encoded in SSA form (symbols incarnated ...)
        if (RunDetector.isRunning()) {
            int beforeDepth = depthDelta > 0 ? 1 : 0;
            int afterDepth = depthDelta < 0 ? 1 : 0;

            Set<AccessExpression> exprs = new HashSet<AccessExpression>();
            Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

            from.addAccessExpressionsToSet(exprs);

            for (AccessExpression e : exprs) {
                replacements.put(e, ssa.getSymbolIncarnation(e, beforeDepth));
            }

            from = from.replace(replacements);

            if (to instanceof Root) {
                ssa.createNewSymbolIncarnation(to, afterDepth);

                return VariableAssign.create((Root) ssa.getSymbolIncarnation(to, afterDepth), from);
            } else if (to instanceof ObjectFieldRead) {
                ObjectFieldRead fr = (ObjectFieldRead) to;
                Field f = fr.getField();

                Field rightHandSide = DefaultObjectFieldWrite.create(
                    ssa.getSymbolIncarnation(fr.getObject(), beforeDepth),
                    ssa.getFieldIncarnation(f),
                    from
                );

                ssa.createNewSymbolIncarnation(to, afterDepth);

                return FieldAssign.create(ssa.getFieldIncarnation(f), rightHandSide);
            } else if (to instanceof ArrayElementRead) {
                ArrayElementRead ar = (ArrayElementRead) to;
                Arrays a = ar.getArrays();

                exprs.clear();
                replacements.clear();

                ar.getIndex().addAccessExpressionsToSet(exprs);

                for (AccessExpression e : exprs) {
                    replacements.put(e, ssa.getSymbolIncarnation(e, beforeDepth));
                }

                Expression index = ar.getIndex().replace(replacements);

                Arrays rightHandSide = DefaultArrayElementWrite.create(
                    ssa.getSymbolIncarnation(ar.getArray(), beforeDepth),
                    ssa.getArraysIncarnation(a),
                    index,
                    from
                );

                ssa.createNewSymbolIncarnation(to, afterDepth);

                return ArraysAssign.create(ssa.getArraysIncarnation(a), rightHandSide);
            }
        }

        return Tautology.create();
    }

    public void extendTraceFormulaWithConstraint(Predicate p, MethodInfo m, int nextPC) {
        extendTraceFormulaWithConstraint(p, m, nextPC, false);
    }

    public void extendTraceFormulaWithConstraint(Predicate p, MethodInfo m, int nextPC, boolean forceConstraint) {
        if (RunDetector.isRunning()) {
            Set<AccessExpression> exprs = new HashSet<AccessExpression>();
            Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

            p.addAccessExpressionsToSet(exprs);

            for (AccessExpression expr : exprs) {
                replacements.put(expr, ssa.getSymbolIncarnation(expr, 0));
            }

            extendTraceFormulaWith(p.replace(replacements), m, nextPC, forceConstraint);
        }
    }

    private void extendTraceFormulaWith(Predicate p, MethodInfo m, int nextPC, boolean forceConstraint) {
        if (forceConstraint || !(p instanceof Tautology)) {
            traceFormula.append(p, m, nextPC);
        }
    }

    public void cutTraceAfterAssertion(MethodInfo m, int pc) {
        traceFormula.cutAfterAssertion(m, pc);
    }

    @Override
    public void processPrimitiveStore(MethodInfo lastM, int lastPC, MethodInfo nextM, int nextPC, Expression from, AccessExpression to) {
        Set<AccessExpression> affected = symbolTable.processPrimitiveStore(from, to);

        predicateValuation.reevaluate(lastPC, nextPC, to, affected, from);

        extendTraceFormulaWithAssignment(to, from, nextM, nextPC, 0);
    }

    @Override
    public void processObjectStore(MethodInfo lastM, int lastPC, MethodInfo nextM, int nextPC, Expression from, AccessExpression to) {
        Set<AccessExpression> affected = symbolTable.processObjectStore(from, to);

        predicateValuation.reevaluate(lastPC, nextPC, to, affected, from);

        extendTraceFormulaWithAssignment(to, from, nextM, nextPC, 0);
    }

    @Override
    public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        symbolTable.processMethodCall(threadInfo, before, after);

        predicateValuation.processMethodCall(threadInfo, before, after);

        visitedMethods.add(after.getMethodInfo());

        if (RunDetector.isRunning()) {
            ssa.changeDepth(+1);
            MethodInfo method = after.getMethodInfo();
            byte[] argTypes = new byte[method.getNumberOfStackArguments()];

            int i = 0;

            if (!method.isStatic()) {
                argTypes[i++] = Types.T_REFERENCE;
            }

            for (byte argType : method.getArgumentTypes()) {
                argTypes[i++] = argType;
            }

            Predicate assignment = Tautology.create();

            for (int argIndex = 0, slotIndex = 0; argIndex < method.getNumberOfStackArguments(); ++argIndex) {
                Expression expr = ExpressionUtil.getExpression(after.getSlotAttr(slotIndex));

                // Actual symbolic parameter
                LocalVarInfo arg = after.getLocalVarInfo(slotIndex);
                String name = arg == null ? null : arg.getName();

                AccessExpression formalArgument = DefaultRoot.create(name, slotIndex);

                if (expr != null) {
                    assignment = Conjunction.create(assignment, getTraceFormulaAssignmentConjunct(formalArgument, expr, +1));
                }

                switch (argTypes[argIndex]) {
                    case Types.T_LONG:
                    case Types.T_DOUBLE:
                        slotIndex += 2;
                        break;

                    default:
                        slotIndex += 1;
                        break;
                }
            }

            traceFormula.markCallInvoked();

            extendTraceFormulaWith(assignment, method, 0, false);

            traceFormula.markCallStarted();
        }
    }

    public Integer computePreciseExpressionValue(Expression expression) {
        return predicateValuation.evaluateExpression(expression);
    }

    public int[] computeAllExpressionValuesInRange(Expression expression, int lowerBound, int upperBound) {
        return predicateValuation.evaluateExpressionInRange(expression, lowerBound, upperBound);
    }

    @Override
    public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        symbolTable.processMethodReturn(threadInfo, before, after);

        if (RunDetector.isRunning()) {
            MethodInfo callee = before.getMethodInfo();
            MethodInfo caller = after.getMethodInfo();
            AccessExpression returnSymbolCallee = DefaultReturnValue.create();
            AccessExpression returnSymbolCaller = DefaultReturnValue.create(after.getPC());
            Expression returnValue;

            if (before.getMethodInfo().getReturnSize() == 2) {
                returnValue = ExpressionUtil.getExpression(after.getLongResultAttr());
            } else {
                returnValue = ExpressionUtil.getExpression(after.getResultAttr());
            }

            if (before.getMethodInfo().getReturnSize() > 0) {
                extendTraceFormulaWithAssignment(returnSymbolCallee, returnValue, callee, before.getPC().getPosition() + before.getPC().getLength(), 0);
            }

            traceFormula.markReturn();

            if (before.getMethodInfo().getReturnSize() > 0) {
                extendTraceFormulaWithAssignment(returnSymbolCaller, returnSymbolCallee, caller, after.getPC().getPosition(), -1);
            }

            ssa.changeDepth(-1);

            traceFormula.markReturned();
        }

        predicateValuation.processMethodReturn(threadInfo, before, after);
    }

    @Override
    public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        symbolTable.processVoidMethodReturn(threadInfo, before, after);
        predicateValuation.processVoidMethodReturn(threadInfo, before, after);

        if (RunDetector.isRunning()) {
            ssa.changeDepth(-1);

            traceFormula.markReturn();
            traceFormula.markReturned();
        }
    }

    @Override
    public TruthValue processBranchingCondition(int lastPC, BranchingCondition condition) {
        Predicate predicate = (Predicate) condition;

        return predicateValuation.evaluatePredicate(lastPC, predicate);
    }

    @Override
    public void processNewClass(ThreadInfo thread, ClassInfo classInfo) {
        if (isInitialized) {
            symbolTable.get(0).addClass(classInfo.getStaticElementInfo(), thread);

            if (RunDetector.isRunning() && PandaConfig.getInstance().initializeStaticFields()) {
                int lastPC = thread.getPC().getPosition();
                int nextPC = thread.getPC().getNext().getPosition();

                ClassName cn = new ClassName(classInfo.getStaticElementInfo());
                PackageAndClass pkgcn = DefaultPackageAndClass.create(cn.getClassName());

                StructuredValue v = symbolTable.get(0).getUniverse().get(cn);
                Predicate constraint = Tautology.create();

                Set<AccessExpression> affected = new HashSet<AccessExpression>();

                for (UniverseSlotKey k : v.getSlots().keySet()) {
                    FieldName fn = (FieldName) k;

                    if (!ignoredStaticFields.contains(fn.getName())) {
                        ObjectFieldRead fr = DefaultObjectFieldRead.create(pkgcn, fn.getName());

                        affected.clear();
                        affected.add(fr);

                        if (v.getSlot(k) instanceof StructuredValueSlot) {
                            predicateValuation.reevaluate(lastPC, nextPC, fr, affected, NullExpression.create());

                            constraint = Conjunction.create(constraint, getTraceFormulaAssignmentConjunct(fr, NullExpression.create(), 0));
                        } else {
                            predicateValuation.reevaluate(lastPC, nextPC, fr, affected, Constant.create(0));

                            constraint = Conjunction.create(constraint, getTraceFormulaAssignmentConjunct(fr, Constant.create(0), 0));
                        }
                    }
                }

                extendTraceFormulaWith(constraint, thread.getTopFrameMethodInfo(), thread.getPC().getPosition(), false);
            }
        } else {
            startupClasses.add(classInfo);
        }
    }

    @Override
    public void processObject(AnonymousObject object, MethodInfo m, int pc) {
        symbolTable.get(0).addObject(object);
        predicateValuation.get(0).addObject(object);

        if (object instanceof AnonymousArray) {
            AnonymousArray a = (AnonymousArray) object;

            extendTraceFormulaWithConstraint((Equals) Equals.create(DefaultArrayLengthRead.create(a), a.getArrayLength()), m, pc);
        }
    }

    public enum Initialization {
        READ,
        WRITE
    }

    @Override
    public void processNewObject(AnonymousObject object, MethodInfo m, int pc) {
        Set<AccessExpression> exprs = new HashSet<AccessExpression>();

        for (Step s : traceFormula) {
            s.getPredicate().addAccessExpressionsToSet(exprs);
        }

        processObject(object, m, pc);

        Predicate fresh = Tautology.create();

        for (AccessExpression ae : exprs) {
            if (!(ae instanceof ObjectFieldWrite) && !(ae instanceof ArrayElementWrite) && !(ae instanceof ArrayLengthWrite)) {
                fresh = Conjunction.create(fresh, Negation.create(Equals.create(ae, object)));
            }
        }

        Predicate constraint = Tautology.create();

        boolean initO = PandaConfig.getInstance().initializeObjectFields();
        boolean initA = PandaConfig.getInstance().initializeArrayElements();

        if (RunDetector.isRunning()) {
            if (initO || initA) {
                StructuredValue v = symbolTable.get(0).getUniverse().get(object.getReference());

                initO &= v instanceof UniverseObject;
                initA &= v instanceof UniverseArray;

                if (initA) {
                    UniverseArray a = (UniverseArray) v;
                    Arrays arr = DefaultArrays.create();
                    Arrays arrDef = ssa.getArraysIncarnation(DefaultArrays.create());

                    for (UniverseSlotKey k : v.getSlots().keySet()) {
                        Expression index = Constant.create(((ElementIndex)k).getIndex());

                        Expression val;

                        if (v.getSlot(k) instanceof StructuredValueSlot) {
                            val = NullExpression.create();
                        } else {
                            val = Constant.create(0);
                        }

                        switch (PandaConfig.getInstance().refinementInitializationType()) {
                            case READ:
                                constraint = Conjunction.create(constraint, Equals.create(ssa.getSymbolIncarnation(DefaultArrayElementRead.create(object, index), 0), val));
                                break;
                            case WRITE:
                                arrDef = DefaultArrayElementWrite.create(
                                    ssa.getSymbolIncarnation(object, 0),
                                    arrDef,
                                    index,
                                    val
                                );
                                ssa.createNewSymbolIncarnation(DefaultArrayElementRead.create(object, arr, Constant.create(0)), 0);
                                break;
                        }
                    }

                    switch (PandaConfig.getInstance().refinementInitializationType()) {
                        case READ:
                            break;
                        case WRITE:
                            constraint = Conjunction.create(constraint, ArraysAssign.create(ssa.getArraysIncarnation(arr), arrDef));
                            break;
                    }
                } else if (initO) {
                    for (UniverseSlotKey k : v.getSlots().keySet()) {
                        FieldName fn = (FieldName) k;

                        ObjectFieldRead fr = DefaultObjectFieldRead.create(object, fn.getName());

                        Expression val;

                        if (v.getSlot(k) instanceof StructuredValueSlot) {
                            val = NullExpression.create();
                        } else {
                            val = Constant.create(0);
                        }

                        switch (PandaConfig.getInstance().refinementInitializationType()) {
                            case READ:
                                constraint = Conjunction.create(constraint, Equals.create(ssa.getSymbolIncarnation(fr, 0), val));
                                break;
                            case WRITE:
                                constraint = Conjunction.create(constraint, getTraceFormulaAssignmentConjunct(fr, val, 0));
                                break;
                        }
                    }
                }
            }
        }

        extendTraceFormulaWith(Conjunction.create(fresh, constraint), m, pc, false);
    }

    @Override
    public void informAboutPrimitiveLocalVariable(Root root) {
        symbolTable.get(0).ensurePrimitiveLocalVariableExistence(root);
    }

    @Override
    public void informAboutStructuredLocalVariable(Root root) {
        symbolTable.get(0).ensureStructuredLocalVariableExistence(root);
    }

    @Override
    public void informAboutBranchingDecision(BranchingDecision decision, MethodInfo m, int nextPC) {
        if (!RunDetector.isRunning()) return;

        BranchingConditionValuation bcv = (BranchingConditionValuation) decision;
        Predicate condition = bcv.getCondition();
        TruthValue value = bcv.getValuation();

        predicateValuation.force(condition, value);

        if (value == TruthValue.FALSE) {
            condition = Negation.create(condition);
        }

        extendTraceFormulaWithConstraint(condition, m, nextPC);
    }

    @Override
    public void addThread(ThreadInfo threadInfo) {
        symbolTable.addThread(threadInfo);
        predicateValuation.addThread(threadInfo);
    }

    @Override
    public void scheduleThread(ThreadInfo threadInfo) {
        symbolTable.scheduleThread(threadInfo);
        predicateValuation.scheduleThread(threadInfo);
    }

    public SystemSymbolTable getSymbolTable() {
        return symbolTable;
    }

    public SystemPredicateValuation getPredicateValuation() {
        return predicateValuation;
    }

    @Override
    public void start(ThreadInfo mainThread) {
        Map<Integer, SymbolTableStack> symbols = new HashMap<Integer, SymbolTableStack>();
        Map<Integer, PredicateValuationStack> predicates = new HashMap<Integer, PredicateValuationStack>();

        // Initial state for last backtrack
        State state = new State(
            mainThread.getId(),
            symbols,
            predicates,
            traceFormula,
            ssa,
            new HashSet<MethodInfo>(),
            forcedTrace
        );

        trace.push(state);
        traceFormula.markState();

        /**
         * Register the main thread as it is not explicitely created elsewhere
         */
        addThread(mainThread);
        scheduleThread(mainThread);

        isInitialized = true;

        for (ClassInfo cls : startupClasses) {
            processNewClass(mainThread, cls);
        }
    }

    public TraceFormula getTraceFormula() {
        return traceFormula;
    }

    public void forceStatesAlongTrace(TraceFormula tf) {
        forcedTrace = tf;
    }

    public void dropForceStatesAlongTrace() {
        forcedTrace = null;
    }

    public TraceFormula getForcedTraceFormula() {
        return forcedTrace;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forward(MethodInfo method) {
        Set<MethodInfo> visitedMethods = new HashSet<MethodInfo>();

        visitedMethods.addAll(this.visitedMethods);

        State state = new State(
            VM.getVM().getCurrentThread().getId(),
            symbolTable.memorize(),
            predicateValuation.memorize(),
            traceFormula.clone(),
            ssa.clone(),
            visitedMethods,
            forcedTrace
        );

        trace.push(state);
        traceFormula.markState();

        if (forcedTrace != null) {
            if (traceFormula.isPrefixOf(forcedTrace)) {
                VM.getVM().forceState();
            } else {
                forcedTrace = null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void backtrack(MethodInfo method) {
        trace.pop();

        symbolTable.restore(trace.top().symbolTableStacks);
        symbolTable.scheduleThread(trace.top().currentThread);
        predicateValuation.restore(trace.top().predicateValuationStacks);
        predicateValuation.scheduleThread(trace.top().currentThread);

        traceFormula = trace.top().traceFormula.clone();
        ssa = trace.top().ssa.clone();
        visitedMethods.clear();
        visitedMethods.addAll(trace.top().visitedMethods);
        forcedTrace = trace.top().forcedTrace;

        if (PandaConfig.getInstance().keepExploredBranches()) {
            history.backtrack();
        }

        if (trace.isEmpty()) {
            smt.close();
        }
    }

    public Trace getTrace() {
        return trace;
    }

    public void collectGarbage(VM vm, ThreadInfo threadInfo) {
        if (isInitialized) {
            symbolTable.collectGarbage(vm, threadInfo);
        }
    }

    public int getNumberOfRefinements() {
        return refinements;
    }

    boolean dropped = false;

    /**
     * @returns null if real error, else a backtrack level is returned (the depth in the search to which to return)
     */
    public Integer error() {
        PandaConfig config = PandaConfig.getInstance();

        if (config.enabledRefinement()) {
            if (config.enabledVerbose(this.getClass())) {
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println("Error source code line: " + VM.getVM().getCurrentThread().getPC().getSourceLine());
                System.out.println("Trying to refine abstraction.");
                System.out.println();
            }

            Predicate[] interpolants = smt.interpolate(traceFormula);

            notifyAboutCounterexample(traceFormula, interpolants);

            if (interpolants != null) {
                refinements++;

                boolean refined = false;
                int refinedAt = traceFormula.get(traceFormula.size() - 1).getDepth();
                int refinedMethodAt = trace.size();

                List<MethodInfo> refinedMethods = new ArrayList<MethodInfo>();

                for (int i = 0; i < interpolants.length; ++i) {
                    Predicate interpolant = interpolants[i];

                    // Make the predicate valid at the correct point in the trace (needs to be valid over instructions that follow but do not contribute to the trace formula (stack manipulation))
                    MethodInfo mStart = traceFormula.get(i).getMethod();
                    int pcStart = traceFormula.get(i).getPC();

                    MethodInfo mEnd = null;
                    int pcEnd = mStart.getLastInsn().getPosition();

                    if (traceFormula.size() > i) {
                        mEnd = traceFormula.get(i + 1).getMethod();

                        if (mStart == mEnd) {
                            pcEnd = traceFormula.get(i + 1).getPC();
                        }
                    }

                    if (config.enabledVerbose(this.getClass())) {
                        System.out.println("Adding predicate `" + interpolant + "` to [" + pcStart + ", " + pcEnd + "]");

                        if (interpolant instanceof Tautology || interpolant instanceof Contradiction) {
                            System.out.println();
                        }
                    }

                    boolean refinedOnce = heuristic.refine(interpolant, mStart, pcStart, pcEnd);

                    if (refinedOnce) {
                        refined = true;
                        refinedAt = Math.min(refinedAt, traceFormula.get(i).getDepth());

                        boolean contained = false;

                        for (MethodInfo m : refinedMethods) {
                            if (m.getFullName().equals(mStart.getFullName())) {
                                contained = true;
                                break;
                            }
                        }

                        if (!contained) {
                            refinedMethods.add(mStart);
                        }
                    }
                }

                boolean drop = false;

                if (!refined) {
                    if (config.enabledVerbose(this.getClass())) {
                        System.out.println(predicateSet);
                    }

                    String filename = config.refinementDumpAbstractionPredicatesTo();

                    try (PrintWriter w = new PrintWriter(new FileOutputStream(filename))) {
                       w.println(predicateSet.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if ((config.keepUnrefinedPrefix() || config.keepUnrefinedMethodPrefix()) && config.dropUnrefinedPrefixOnFailure() && !dropped) {
                        drop = true;
                        System.out.println("Failed to refine abstraction (possible cycle). Trying to recompute entire state space with the latest abstraction.");
                    } else if (config.printErrorOnRefinementFailure()) {
                        System.out.println("Failed to refine abstraction (possible cycle).");

                        return null;
                    } else {
                        PredicateAbstractionRefinementSearch search = (PredicateAbstractionRefinementSearch) VM.getVM().getSearch();
                        Path path = VM.getVM().getPath().clone();
                        Trace trace = this.trace.clone();
                        int i = path.size() - 1;

                        // Find the first state, where the trace becomes infeasible
                        while (this.trace.size() > 1) {
                            SpecialVariable v = SpecialVariable.create("FeasibilityCheck");

                            if (smt.getModels(traceFormula.toConjunction(), new AccessExpression[] {v}) == null) {
                                i = VM.getVM().getPath().size() - 1;
                                trace = this.trace.clone();
                            }

                            search.performBacktrack();
                        }

                        // Recover the state of Panda's trace (beware: not the whole state of Panda) to be able to print specific info from PredicateValuation etc
                        this.trace = trace;

                        int size = path.size();

                        while (path.size() - 1 > i) {
                            path.removeLast();
                        }

                        // Dump the prefix of the error trace
                        PredicateConsolePublisher.publishTrace(System.out, path);

                        if (path.size() != size) {
                            System.out.println("[WARNING] The above proper prefix of the error trace is already inconsistent, the execution possibly failed to rule out this trace based on predicates that should have already block it.");
                            System.out.println();
                        }

                        throw new JPFException("Failed to refine abstraction (possible cycle).");
                    }
                }

                for (int i = trace.size() - 1; i >= 0; --i) {
                    for (MethodInfo visitedMethod : trace.get(i).visitedMethods) {
                        for (MethodInfo refinedMethod : refinedMethods) {
                            if (visitedMethod.getFullName().equals(refinedMethod.getFullName())) {
                                refinedMethodAt = i;
                            }
                        }
                    }
                }

                /*
                System.out.println("RefinedAt = " + refinedAt + "/" + traceFormula.getDepth() + ", RefinedMethodAt = " + refinedMethodAt + "/" + trace.size());
                if (refinedAt < refinedMethodAt) {
                    // Something unexpected happens
                    System.out.println("Refined:");
                    for (MethodInfo m : refinedMethods) {
                        System.out.println("\t" + m.getName());
                    }
                    System.out.println("Visited:");
                    for (int i = 0; i < trace.size(); ++i) {
                        System.out.print(i + ":");
                        for (MethodInfo m : trace.get(i).visitedMethods) {
                            System.out.print(" " + m.getName());
                        }
                        System.out.println();
                    }

                    boolean printAt = false;
                    boolean printMethodAt = false;
                    for (int i = 0; i < traceFormula.size(); ++i) {
                        System.out.print(traceFormula.get(i).getDepth() + ": " + traceFormula.get(i).getMethod().getName() + ": " + traceFormula.get(i).getPredicate());
                        if (traceFormula.get(i).getDepth() == refinedAt && !printAt) {
                            System.out.print("\t <<<<<< STATE REF");
                            printAt = true;
                        }
                        if (traceFormula.get(i).getDepth() == refinedMethodAt && !printMethodAt) {
                            System.out.print("\t <<<<<< METHOD REF");
                            printMethodAt = true;
                        }
                        System.out.println();
                    }
                    try {
                        System.out.println("[ENTER]");
                        System.in.read();
                    } catch (Exception e) {
                    }
                }
                */

                /**
                 * At this point backtrackLevel points to the first refined level of the search
                 *
                 * We need to backtrack to the last not refined level, however
                 */
                int backtrackLevel;

                if (config.keepUnrefinedPrefix() && config.keepUnrefinedMethodPrefix() && !drop) {
                    backtrackLevel = Math.max(refinedAt, refinedMethodAt) - 1;
                    dropped = false;
                } else if (config.keepUnrefinedMethodPrefix() && !drop) {
                    backtrackLevel = refinedMethodAt - 1;
                    dropped = false;
                } else if (config.keepUnrefinedPrefix() && !drop) {
                    backtrackLevel = refinedAt - 1;
                    dropped = false;
                } else {
                    backtrackLevel = 1;
                    dropped = true;
                }

                /**
                 * The first step of the interpolant should never be contradiction
                 * Thus the refinement should never happen before the first step (in the auxiliary state -1, which we need to keep to start our refined search from)
                 */
                if (backtrackLevel < 1) {
                    //throw new RuntimeException("Refinement backtracks too much and will end the search loop.");
                    backtrackLevel = 1;
                }

                notifyAboutRefinementBacktrackLevel(backtrackLevel);

                notifyAboutCurrentPredicateSet(predicateValuation.getPredicateSet(), refinedMethods);

                return backtrackLevel;
            }
        }

        return null;
    }

    private static void notifyAboutCounterexample(TraceFormula traceFormula, Predicate[] interpolants) {
        for (CounterexampleListener listener : listeners) {
            listener.counterexample(traceFormula, interpolants);
        }
    }

    private static void notifyAboutRefinementBacktrackLevel(int lvl) {
        for (CounterexampleListener listener : listeners) {
            listener.backtrackLevel(lvl);
        }
    }

    private static void notifyAboutCurrentPredicateSet(Predicates predicateSet, List<MethodInfo> refinedMethods) {
        for (CounterexampleListener listener : listeners) {
            listener.currentPredicateSet(predicateSet, refinedMethods);
        }
    }

}
