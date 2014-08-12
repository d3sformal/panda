package gov.nasa.jpf.abstraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.BranchingCondition;
import gov.nasa.jpf.abstraction.common.BranchingConditionInfo;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.common.BranchingDecision;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.ObjectExpressionDecorator;
import gov.nasa.jpf.abstraction.common.impl.PrimitiveExpressionDecorator;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.state.PredicateValuationStack;
import gov.nasa.jpf.abstraction.state.State;
import gov.nasa.jpf.abstraction.state.SymbolTableStack;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;
import gov.nasa.jpf.abstraction.state.SystemSymbolTable;
import gov.nasa.jpf.abstraction.state.Trace;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueIdentifier;
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
    private boolean isInitialized = false;
    private Set<ClassInfo> startupClasses = new HashSet<ClassInfo>();

    public SMT smt = new SMT();
    private Stack<Pair<MethodInfo, Integer>> traceProgramLocations = new Stack<Pair<MethodInfo, Integer>>();
    private Predicate traceFormula = Tautology.create();
    private Predicates predicateSet;

    private static PredicateAbstraction instance = null;

    public static void setInstance(PredicateAbstraction instance) {
        PredicateAbstraction.instance = instance;
    }

    public static PredicateAbstraction getInstance() {
        return instance;
    }

    public PredicateAbstraction(Predicates predicateSet) {
        symbolTable = new SystemSymbolTable(this);
        predicateValuation = new SystemPredicateValuation(this, predicateSet);
        trace = new Trace();

        this.predicateSet = predicateSet;
    }

    private StaticSingleAssignmentFormulaFormatter ssa = new StaticSingleAssignmentFormulaFormatter();

    private void extendTraceFormulaWithAssignment(AccessExpression to, Expression from, MethodInfo m, int nextPC) {
        // a := ...
        // a' = ...
        //
        // a.f := ...
        // f' = fwrite(f, a, ...)
        //
        // a[i] := ...
        // arr' = awrite(arr, a, i, ...)
        if (RunDetector.isRunning()) {
            Set<AccessExpression> exprs = new HashSet<AccessExpression>();
            Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

            from.addAccessExpressionsToSet(exprs);

            for (AccessExpression e : exprs) {
                replacements.put(e, ssa.incarnateSymbol(e));
            }

            from = from.replace(replacements);

            if (to instanceof Root) {
                ssa.reincarnateSymbol(to);

                extendTraceFormulaWith(VariableAssign.create((Root) ssa.incarnateSymbol(to), from), m, nextPC);
            } else if (to instanceof ObjectFieldRead) {
                ObjectFieldRead fr = (ObjectFieldRead) to;
                Field f = fr.getField();

                Field rightHandSide = DefaultObjectFieldWrite.create(
                    ssa.incarnateSymbol(fr.getObject()),
                    ssa.incarnateSymbol(f),
                    from
                );

                ssa.reincarnateSymbol(to);

                extendTraceFormulaWith(FieldAssign.create(ssa.incarnateSymbol(f), rightHandSide), m, nextPC);
            } else if (to instanceof ArrayElementRead) {
                ArrayElementRead ar = (ArrayElementRead) to;
                Arrays a = ar.getArrays();

                exprs.clear();
                replacements.clear();

                ar.getIndex().addAccessExpressionsToSet(exprs);

                for (AccessExpression e : exprs) {
                    replacements.put(e, ssa.incarnateSymbol(e));
                }

                Expression index = ar.getIndex().replace(replacements);

                Arrays rightHandSide = DefaultArrayElementWrite.create(
                    ssa.incarnateSymbol(ar.getArray()),
                    ssa.incarnateSymbol(a),
                    index,
                    from
                );

                ssa.reincarnateSymbol(to);

                extendTraceFormulaWith(ArraysAssign.create(ssa.incarnateSymbol(a), rightHandSide), m, nextPC);
            }
        }
    }

    private void extendTraceFormulaWithConstraint(Equals e, MethodInfo m, int nextPC) {
        if (RunDetector.isRunning()) {
            Expression e1 = e.a;
            Expression e2 = e.b;

            Set<AccessExpression> exprs = new HashSet<AccessExpression>();
            Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

            e1.addAccessExpressionsToSet(exprs);
            e2.addAccessExpressionsToSet(exprs);

            for (AccessExpression expr : exprs) {
                replacements.put(expr, ssa.incarnateSymbol(expr));
            }

            extendTraceFormulaWith(e.replace(replacements), m, nextPC);
        }
    }

    private void extendTraceFormulaWith(Predicate p, MethodInfo m, int nextPC) {
        traceProgramLocations.push(new Pair<MethodInfo, Integer>(m, nextPC));
        traceFormula = Conjunction.create(traceFormula, p);
    }

    @Override
    public void processPrimitiveStore(MethodInfo m, int lastPC, int nextPC, Expression from, AccessExpression to) {
        Set<AccessExpression> affected = symbolTable.processPrimitiveStore(from, to);

        predicateValuation.reevaluate(lastPC, nextPC, to, affected, PrimitiveExpressionDecorator.wrap(from, symbolTable));

        extendTraceFormulaWithAssignment(to, from, m, nextPC);
    }

    @Override
    public void processObjectStore(MethodInfo m, int lastPC, int nextPC, Expression from, AccessExpression to) {
        Set<AccessExpression> affected = symbolTable.processObjectStore(from, to);

        predicateValuation.reevaluate(lastPC, nextPC, to, affected, ObjectExpressionDecorator.wrap(from, symbolTable));

        extendTraceFormulaWithAssignment(to, from, m, nextPC);
    }

    @Override
    public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        symbolTable.processMethodCall(threadInfo, before, after);

        predicateValuation.processMethodCall(threadInfo, before, after);
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
        predicateValuation.processMethodReturn(threadInfo, before, after);
    }

    @Override
    public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        symbolTable.processVoidMethodReturn(threadInfo, before, after);
        predicateValuation.processVoidMethodReturn(threadInfo, before, after);
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
        } else {
            startupClasses.add(classInfo);
        }
    }

    @Override
    public void processNewObject(AnonymousObject object) {
        symbolTable.get(0).addObject(object);
        predicateValuation.get(0).addObject(object);

        if (object instanceof AnonymousArray) {
            AnonymousArray a = (AnonymousArray) object;

            extendTraceFormulaWithConstraint((Equals) Equals.create(DefaultArrayLengthRead.create(a), a.getArrayLength()), ThreadInfo.getCurrentThread().getPC().getMethodInfo(), ThreadInfo.getCurrentThread().getPC().getPosition());
        }
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

        Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();
        Set<AccessExpression> exprs = new HashSet<AccessExpression>();

        condition.addAccessExpressionsToSet(exprs);

        for (AccessExpression e : exprs) {
            replacements.put(e, ssa.incarnateSymbol(e));
        }

        condition = condition.replace(replacements);

        extendTraceFormulaWith(condition, m, nextPC);
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
            traceProgramLocations,
            traceFormula,
            ssa
        );

        trace.push(state);

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

    public Predicate getTraceFormula() {
        return traceFormula;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forward(MethodInfo method) {
        State state = new State(
            VM.getVM().getCurrentThread().getId(),
            symbolTable.memorize(),
            predicateValuation.memorize(),
            (Stack<Pair<MethodInfo, Integer>>)traceProgramLocations.clone(),
            traceFormula,
            ssa.clone()
        );

        trace.push(state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void backtrack(MethodInfo method) {
        trace.pop();

        symbolTable.restore(trace.top().symbolTableStacks);
        symbolTable.scheduleThread(trace.top().currentThread);
        predicateValuation.restore(trace.top().predicateValuationStacks);
        predicateValuation.scheduleThread(trace.top().currentThread);

        traceProgramLocations = (Stack<Pair<MethodInfo, Integer>>)trace.top().traceProgramLocations.clone();
        traceFormula = trace.top().traceFormula;
        ssa = trace.top().ssa.clone();

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

    private static int traceStep;
    private static int traceStepCount;

    public boolean error() {
        System.out.println();
        System.out.println();
        System.out.println("Counterexample Trace Formula:");
        //System.out.println(traceFormula.toString(Notation.SMT_NOTATION));
        //System.out.println(traceFormula.toString(Notation.FUNCTION_NOTATION));

        traceStep = 0;
        traceStepCount = countErrorConjuncts(traceFormula);
        printErrorConjuncts(traceFormula);

        if (VM.getVM().getJPF().getConfig().getBoolean("panda.interpolation")) {
            Predicate[] interpolants = smt.interpolate(traceFormula);

            System.out.println();
            System.out.println("Feasible: " + (interpolants == null));

            if (interpolants != null) {
                int maxLen = 0;

                for (int i = 0; i < interpolants.length; ++i) {
                    int pcLen = ("[" + traceProgramLocations.get(i).getFirst().getName() + ":" + traceProgramLocations.get(i).getSecond() + "]").length();

                    if (maxLen < pcLen) {
                        maxLen = pcLen;
                    }
                }

                for (int i = 0; i < interpolants.length; ++i) {
                    Predicate interpolant = interpolants[i];
                    int pc = traceProgramLocations.get(i).getSecond();
                    String pcStr = "[" + traceProgramLocations.get(i).getFirst().getName() + ":" + traceProgramLocations.get(i).getSecond() + "]";
                    int pcLen = pcStr.length();

                    System.out.print("\t" + pcStr + ": ");
                    for (int j = 0; j < maxLen - pcLen; ++j) {
                        System.out.print(" ");
                    }
                    System.out.println(interpolant);
                }

                boolean refined = false;

                for (int i = 0; i < interpolants.length; ++i) {
                    Predicate interpolant = interpolants[i];

                    // Make the predicate valid at the correct point in the trace (needs to be valid over instructions that follow but do not contribute to the trace formula (stack manipulation))
                    MethodInfo mStart = traceProgramLocations.get(i).getFirst();
                    int pcStart = traceProgramLocations.get(i).getSecond();

                    MethodInfo mEnd = null;
                    int pcEnd = mStart.getLastInsn().getPosition();

                    if (traceProgramLocations.size() > i) {
                        mEnd = traceProgramLocations.get(i + 1).getFirst();

                        if (mStart == mEnd) {
                            pcEnd = traceProgramLocations.get(i + 1).getSecond();
                        }
                    }

                    //System.out.println("Adding predicate `" + interpolant + "` to [" + pcStart + ", " + pcEnd + "]");
                    boolean refinedOnce = predicateValuation.refine(interpolant, mStart, pcStart, pcEnd);

                    refined = refined || refinedOnce;
                }

                if (!refined) {
                    System.out.println(predicateSet);
                    throw new RuntimeException("Failed to refine abstraction.");
                }

                System.out.println();

                return false;
            }

            System.out.println();
            System.out.println();
        }

        return true;
    }

    private static int countErrorConjuncts(Predicate formula) {
        if (formula instanceof Conjunction) {
            Conjunction c = (Conjunction) formula;

            return countErrorConjuncts(c.a) + countErrorConjuncts(c.b);
        } else {
            return 1;
        }
    }

    private static int log(int n) {
        int i = 0;

        while (n > 0) {
            n /= 10;

            ++i;
        }

        return i;
    }

    private static void printErrorConjuncts(Predicate formula) {
        if (formula instanceof Conjunction) {
            Conjunction c = (Conjunction) formula;

            printErrorConjuncts(c.a);
            printErrorConjuncts(c.b);
        } else {
            System.out.print("\t");

            ++traceStep;

            for (int i = 0; i < log(traceStepCount) - log(traceStep); ++i) {
                System.out.print(" ");
            }

            System.out.println(traceStep + ": " + formula.toString(Notation.FUNCTION_NOTATION));
        }
    }
}
