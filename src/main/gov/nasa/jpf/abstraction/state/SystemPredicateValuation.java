package gov.nasa.jpf.abstraction.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.jpf.jvm.bytecode.IINC;
import gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction;
import gov.nasa.jpf.jvm.bytecode.StoreInstruction;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.AbstractMethodPredicateContext;
import gov.nasa.jpf.abstraction.common.AssumePredicateContext;
import gov.nasa.jpf.abstraction.common.BytecodeInterval;
import gov.nasa.jpf.abstraction.common.BytecodeUnlimitedRange;
import gov.nasa.jpf.abstraction.common.Comparison;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.MethodAssumePostPredicateContext;
import gov.nasa.jpf.abstraction.common.MethodAssumePrePredicateContext;
import gov.nasa.jpf.abstraction.common.MethodPredicateContext;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.ObjectPredicateContext;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.PredicateNotCloneableException;
import gov.nasa.jpf.abstraction.common.PredicateUtil;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.StaticPredicateContext;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultMethod;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.smt.SMTException;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * A predicate valuation aware of method scope changes
 */
public class SystemPredicateValuation implements PredicateValuation, Scoped {

    /**
     * Stacks of scopes (pushed by invoke, poped by return) separately for all threads
     */
    private Map<Integer, PredicateValuationStack> scopes = new HashMap<Integer, PredicateValuationStack>();

    private PredicateAbstraction abstraction;
    private Predicates predicateSet;
    private PredicateValuationMap initialValuation = new PredicateValuationMap();
    private Integer currentThreadID;

    public SystemPredicateValuation(PredicateAbstraction abstraction, Predicates predicateSet) {
        this.abstraction = abstraction;
        this.predicateSet = predicateSet;

        Set<Predicate> predicates = new HashSet<Predicate>();

        for (PredicateContext context : predicateSet.contexts) {
            if (context instanceof AssumePredicateContext) continue;

            predicates.addAll(context.predicates);
        }

        /**
         * Detect initial valuations of Tautologies and Contradictions
         *
         * This is called once and increases comprehensibility of the valuation
         * It could confuse the user to see that a=a is UNKNOWN
         */
        if (!predicates.isEmpty()) {
            try {
                initialValuation.putAll(abstraction.smt.valuatePredicates(predicates));

                for (Predicate predicate : predicates) {
                    // IF NOT A TAUTOLOGY OR CONTRADICTION
                    if (initialValuation.get(predicate) == TruthValue.UNKNOWN) {
                        initialValuation.put(predicate, TruthValue.UNKNOWN);
                    } else {
                        initialValuation.put(predicate, initialValuation.get(predicate));
                    }
                }
            } catch (SMTException e) {
                e.printStackTrace();

                throw e;
            }
        }

        if (initialValuation.isEmpty()) {
            for (Predicate predicate : predicates) {
                initialValuation.put(predicate, TruthValue.UNKNOWN);
            }
        }
    }

    public Predicates getPredicateSet() {
        return predicateSet;
    }

    /**
     * Collect predicates targeted at the given method and store them in the upcoming scope
     */
    @Override
    public MethodFramePredicateValuation createDefaultScope(ThreadInfo threadInfo, MethodInfo method) {
        MethodFramePredicateValuation valuation = new MethodFramePredicateValuation(abstraction.smt);

        if (method == null) return valuation;

        // Collect relevant contexts and predicates stored in them
        // Match context method with actual method
        // Match context object with actual object
        for (PredicateContext context : predicateSet.contexts) {
            if (context instanceof AssumePredicateContext) continue;

            if (context instanceof MethodPredicateContext) {
                MethodPredicateContext methodPredicateContext = (MethodPredicateContext) context;

                if (!methodPredicateContext.getMethod().toString().equals(method.getBaseName())) {
                    continue;
                }
            } else if (context instanceof ObjectPredicateContext) {
                ObjectPredicateContext objectPredicateContext = (ObjectPredicateContext) context;

                if (method.isStatic() || method.isClinit()) {
                    continue;
                }

                if (!objectPredicateContext.getPackageAndClass().toString().equals(method.getClassName())) {
                    continue;
                }
            }

            for (Predicate predicate : context.predicates) {
                valuation.put(predicate, initialValuation.get(predicate));
            }
        }

        return valuation;
    }

    @Override
    public Set<Predicate> getPredicatesInconsistentWith(Predicate predicate, TruthValue value) {
        return scopes.get(currentThreadID).top().getPredicatesInconsistentWith(predicate, value);
    }

    @Override
    public void force(Predicate predicate, TruthValue value) {
        scopes.get(currentThreadID).top().force(predicate, value);
    }

    public boolean refine(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        if (interpolant instanceof Conjunction) {
            Conjunction c = (Conjunction) interpolant;

            boolean a = refine(c.a, m, fromPC, toPC);
            boolean b = refine(c.b, m, fromPC, toPC);

            return a || b;
        } else if (interpolant instanceof Disjunction) {
            Disjunction d = (Disjunction) interpolant;

            boolean a = refine(d.a, m, fromPC, toPC);
            boolean b = refine(d.b, m, fromPC, toPC);

            return a || b;
        } else if (interpolant instanceof Negation) {
            Negation n = (Negation) interpolant;

            return refine(n.predicate, m, fromPC, toPC);
        } else if (interpolant instanceof Tautology) {
            return false;
        } else if (interpolant instanceof Contradiction) {
            return false;
        } else {
            boolean refined = false;

            interpolant.setScope(new BytecodeInterval(fromPC, toPC));

            MethodPredicateContext ctx = null;

            for (PredicateContext candidateCtx : predicateSet.contexts) {
                if (candidateCtx instanceof MethodPredicateContext) {
                    MethodPredicateContext methodPredicateContext = (MethodPredicateContext) candidateCtx;

                    if (methodPredicateContext.getMethod().toString().equals(m.getBaseName())) {
                        ctx = methodPredicateContext;

                        break;
                    }
                }
            }

            if (ctx == null) {
                refined = true;
                List<Predicate> predicates = new LinkedList<Predicate>();
                predicates.add(interpolant);
                ctx = new MethodPredicateContext(DefaultMethod.create(DefaultPackageAndClass.create(m.getClassInfo().getName()), m.getName()), predicates);
                predicateSet.contexts.add(ctx);
            } else {
                boolean present = false;

                for (Predicate p : ctx.predicates) {
                    if (p.equals(interpolant)) {
                        for (int pc : interpolant.getScope()) {
                            refined = refined || !p.isInScope(pc);
                        }

                        p.setScope(p.getScope().merge(interpolant.getScope()));

                        present = true;
                        break;
                    }
                }

                if (!present) {
                    refined = true;
                    ctx.predicates.add(interpolant);
                }
            }

            initialValuation.put(interpolant, TruthValue.UNKNOWN);

            if (VM.getVM().getJPF().getConfig().getBoolean("panda.verbose")) {
                System.out.println("Refined context:");
                System.out.println(gov.nasa.jpf.abstraction.common.Notation.convertToString(ctx));
            }

            return refined;
        }
    }

    @Override
    public void put(Predicate predicate, TruthValue value) {
        scopes.get(currentThreadID).top().put(predicate, value);
    }

    @Override
    public void putAll(Map<Predicate, TruthValue> values) {
        scopes.get(currentThreadID).top().putAll(values);
    }

    @Override
    public void remove(Predicate predicate) {
        scopes.get(currentThreadID).top().remove(predicate);
    }

    @Override
    public TruthValue get(Predicate predicate) {
        return scopes.get(currentThreadID).top().get(predicate);
    }

    private void overrideWithAssumedPreValuation(PredicateValuation valuation, MethodInfo method) {
        overrideWithAssumedValuation(valuation, method, MethodAssumePrePredicateContext.class);
    }

    private void overrideWithAssumedPostValuation(PredicateValuation valuation, MethodInfo method) {
        overrideWithAssumedValuation(valuation, method, MethodAssumePostPredicateContext.class);
    }

    private void overrideWithAssumedValuation(PredicateValuation valuation, MethodInfo method, Class<? extends AssumePredicateContext> assumedClass) {
        // Override with assumed valuation
        for (PredicateContext context : predicateSet.contexts) {
            if (context.getClass().isAssignableFrom(assumedClass)) {
                AbstractMethodPredicateContext methodPredicateContext = (AbstractMethodPredicateContext) context;

                if (!methodPredicateContext.getMethod().toString().equals(method.getBaseName())) {
                    continue;
                }

                for (Predicate predicate : context.predicates) {
                    Set<Predicate> inconsistent = valuation.getPredicatesInconsistentWith(predicate, TruthValue.TRUE);

                    if (!inconsistent.isEmpty()) {
                        System.out.println("Warning: forced value of `" + predicate + "` is inconsistent with " + inconsistent);

                        throw new RuntimeException("Trying to make an inconsistent assumption");
                    }

                    valuation.force(predicate, TruthValue.TRUE);
                }
            }
        }
    }

    @Override
    public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        MethodInfo method = after.getMethodInfo();

        // Scope to be added as the callee scope
        MethodFramePredicateValuation calleeScope = createDefaultScope(threadInfo, method);

        RunDetector.detectRunning(VM.getVM(), after.getPC(), before.getPC());

        if (RunDetector.isRunning()) {
            // Copy of the current caller scope - to avoid modifications - may not be needed now, it is not different from .top() and it is not modified here
            MethodFramePredicateValuation callerScope = scopes.get(currentThreadID).top();

            Map<Predicate, Set<Predicate>> reverseMap = new HashMap<Predicate, Set<Predicate>>();
            Set<Predicate> callerScopePredicates = new HashSet<Predicate>();
            Set<Predicate> unknown = new HashSet<Predicate>();
            Set<AccessExpression> temporaryPathHolder = new HashSet<AccessExpression>();

            /**
             * Take predicates from the callee that describe formal parameters
             * Replace formal parameters with the concrete assignment
             * Reason about the value of the predicates using known values of predicates in the caller
             */
            byte[] argTypes = new byte[method.getNumberOfStackArguments()];

            int i = 0;

            if (!method.isStatic()) {
                argTypes[i++] = Types.T_REFERENCE;
            }

            for (byte argType : method.getArgumentTypes()) {
                argTypes[i++] = argType;
            }

            Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();
            Set<AccessExpression> argumentSymbols = new HashSet<AccessExpression>();

            // Replace formal parameters with actual parameters
            for (int argIndex = 0, slotIndex = 0; argIndex < method.getNumberOfStackArguments(); ++argIndex) {
                Expression expr = ExpressionUtil.getExpression(after.getSlotAttr(slotIndex));

                // Actual symbolic parameter
                LocalVarInfo arg = after.getLocalVarInfo(slotIndex);
                String name = arg == null ? null : arg.getName();

                AccessExpression formalArgument = DefaultRoot.create(name, slotIndex);

                replacements.put(formalArgument, expr);
                argumentSymbols.add(formalArgument);

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

            // Each predicate to be initialised for the callee
            for (Predicate predicate : calleeScope.getPredicates()) {
                predicate.addAccessExpressionsToSet(temporaryPathHolder);

                boolean usesLocals = false;

                for (AccessExpression path : temporaryPathHolder) {
                    if (path.isLocalVariable() && !argumentSymbols.contains(path)) {
                        usesLocals = true;

                        break;
                    }
                }

                // Valuating predicates over uninitialised locals at this point does not make sense
                //
                // We would also identify caller locals with callee locals if they shared names
                // which would result in a wrong valuation
                if (usesLocals) {
                    unknown.add(predicate);
                } else {
                    Predicate callerScopePredicate = predicate.replace(replacements);

                    callerScopePredicates.add(callerScopePredicate);

                    if (!reverseMap.containsKey(callerScopePredicate)) {
                        reverseMap.put(callerScopePredicate, new HashSet<Predicate>());
                    }

                    reverseMap.get(callerScopePredicate).add(predicate);
                }

                temporaryPathHolder.clear();
            }

            // Valuate predicates in the caller scope, and adopt the valuation for the callee predicates
            Map<Predicate, TruthValue> valuation = callerScope.evaluatePredicates(before.getPC().getPosition(), callerScopePredicates);

            for (Predicate callerScopePredicate : callerScopePredicates) {
                for (Predicate calleeScopePredicate : reverseMap.get(callerScopePredicate)) {
                    calleeScope.put(calleeScopePredicate, valuation.get(callerScopePredicate));
                }
            }

            for (Predicate predicate : unknown) {
                calleeScope.put(predicate, initialValuation.get(predicate));
            }

            overrideWithAssumedPreValuation(calleeScope, method);
        }

        scopes.get(currentThreadID).push(method.getFullName(), calleeScope);
    }

    @Override
    public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        RunDetector.detectRunning(VM.getVM(), after.getPC(), before.getPC());

        Expression expr;

        if (before.getMethodInfo().getReturnSize() == 2) {
            expr = ExpressionUtil.getExpression(after.getLongResultAttr());
        } else {
            expr = ExpressionUtil.getExpression(after.getResultAttr());
        }

        if (RunDetector.isRunning()) {
            MethodFramePredicateValuation scope;

            scope = scopes.get(currentThreadID).top(1);

            // Some predicates may have the same determinants, for example:
            //
            //   When processing statement "return 3;"
            //
            //   3 < return
            //   return < 3
            //
            //   Corresponding determinant for both the predicates is
            //
            //   3 < 3
            Map<Predicate, List<Predicate>> predicates = new HashMap<Predicate, List<Predicate>>();
            Set<Predicate> determinants = new HashSet<Predicate>();

            /**
             * Determine values of predicates over the return value based on the concrete symbolic expression being returned
             */
            for (Predicate predicate : getPredicates()) {
                if (PredicateUtil.isPredicateOverReturn(predicate)) {
                    Predicate determinant = predicate.replace(DefaultReturnValue.create(), expr);

                    if (!predicates.containsKey(determinant)) {
                        predicates.put(determinant, new LinkedList<Predicate>());
                    }

                    predicates.get(determinant).add(predicate);
                    determinants.add(determinant);
                }
            }

            // Valuate predicates over `return` access expression using the return expression
            // Predicate: return < 3
            // Statement: return 2
            //
            // return < 3 is determined by 2 < 3
            Map<Predicate, TruthValue> valuation = evaluatePredicates(before.getPC().getPosition(), determinants);

            for (Predicate determinant : valuation.keySet()) {
                for (Predicate determined : predicates.get(determinant)) {
                    put(determined, valuation.get(determinant));
                }
            }

            // The actual write through of the return value performed by processVoidMethodReturn
        }

        ReturnValue returnValueSpecific = DefaultReturnValue.create(after.getPC(), threadInfo.getTopFrameMethodInfo().isReferenceReturnType());

        if (before.getMethodInfo().getReturnSize() == 2) {
            after.setLongOperandAttr(returnValueSpecific);
        } else {
            after.setOperandAttr(returnValueSpecific);
        }


        // The rest is the same as if no return happend
        processVoidMethodReturn(threadInfo, before, after);
    }

    @Override
    public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        if (RunDetector.isRunning()) {
            MethodInfo method = before.getMethodInfo();
            MethodFramePredicateValuation callerScope = scopes.get(currentThreadID).top(1);

            overrideWithAssumedPostValuation(this, method);

            boolean sameObject = before.getThis() == after.getThis();

            // Collect original symbolic arguments of the method
            Set<Root> referenceArgs = new HashSet<Root>();
            Set<Root> notWantedLocalVariables = new HashSet<Root>();

            /**
             * Determine what reference arguments were written to (they contain a different reference from the initial one)
             *
             * those parameters and predicates over them cannot be used to argue about the value of predicates over the initial value back in the caller
             */
            byte[] argTypes = new byte[method.getNumberOfStackArguments()];

            int i = 0;

            if (!method.isStatic()) {
                argTypes[i++] = Types.T_REFERENCE;
            }

            for (byte argType : method.getArgumentTypes()) {
                argTypes[i++] = argType;
            }

            Expression[] originalArguments = (Expression[]) before.getFrameAttr();

            // Replace formal parameters with actual parameters
            for (int argIndex = 0, slotIndex = 0; argIndex < method.getNumberOfStackArguments(); ++argIndex) {
                // Actual symbolic parameter
                LocalVarInfo arg = method.getLocalVar(slotIndex, 0);
                String name = arg == null ? null : arg.getName();

                Root l = DefaultRoot.create(name, slotIndex);

                // Determine type of the arguments
                if (argTypes[argIndex] == Types.T_REFERENCE || argTypes[argIndex] == Types.T_ARRAY) {
                    referenceArgs.add(l);
                }

                Expression originalExpr = originalArguments[argIndex];
                Expression actualExpr = ExpressionUtil.getExpression(before.getLocalAttr(slotIndex));

                boolean different = false;

                different |= originalExpr == null && actualExpr != null;
                different |= originalExpr != null && actualExpr == null;
                different |= originalExpr != null && actualExpr != null && !originalExpr.equals(actualExpr);

                // Someone has changed the argument, we cannot use predicates about it to infer information about the original value supplied by the caller
                if (different) {
                    notWantedLocalVariables.add(l);
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

            /**
             * No predicate containing a callee's local variable (not a parameter) can be used to infer value of predicates in the caller
             */
            // Local variables are out of scope
            for (Instruction instruction : method.getInstructions()) {
                if (instruction instanceof LocalVariableInstruction && instruction instanceof StoreInstruction) {
                    LocalVariableInstruction lvInsn = (LocalVariableInstruction) instruction;

                    notWantedLocalVariables.add(DefaultRoot.create(lvInsn.getLocalVariableName(), lvInsn.getLocalVariableIndex()));
                } else if (instruction instanceof IINC) {
                    IINC iinc = (IINC) instruction;

                    LocalVarInfo lv = method.getLocalVar(iinc.getIndex(), iinc.getPosition() + iinc.getLength());
                    String name = lv == null ? null : lv.getName();

                    notWantedLocalVariables.add(DefaultRoot.create(name, iinc.getIndex()));
                }
            }

            // Arguments that are of a reference type are not bound to the callee scope and may be used to determine truth value of a predicate refering to it
            notWantedLocalVariables.removeAll(referenceArgs);

            // Collection of predicates in callee and caller that have additional value for update of the caller
            MethodFramePredicateValuation relevant = createDefaultScope(null, null);

            Set<AccessExpression> temporaryPathsHolder = new HashSet<AccessExpression>();

            ReturnValue returnValue = DefaultReturnValue.create();
            ReturnValue returnValueSpecific = DefaultReturnValue.create(after.getPC(), threadInfo.getTopFrameMethodInfo().isReferenceReturnType());

            Map<Predicate, TruthValue> calleeReturns = new HashMap<Predicate, TruthValue>();

            // Replace formal parameters present in the predicate with actual expressions
            Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

            for (int argIndex = 0, slotIndex = 0; argIndex < method.getNumberOfStackArguments(); ++argIndex) {
                if (argTypes[argIndex] == Types.T_REFERENCE || argTypes[argIndex] == Types.T_ARRAY) {
                    LocalVarInfo arg = method.getLocalVar(slotIndex, 0);
                    String name = arg == null ? null : arg.getName();

                    Expression actualExpr = ExpressionUtil.getExpression(before.getLocalAttr(slotIndex));
                    AccessExpression formalArgument = DefaultRoot.create(name, slotIndex);

                    replacements.put(formalArgument, actualExpr);
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

            // Filter out predicates from the callee that cannot be used for propagation to the caller
            for (Predicate predicate : getPredicates()) {
                TruthValue value = get(predicate);

                boolean isUnwanted = false;
                boolean isAnonymous = false;

                predicate.addAccessExpressionsToSet(temporaryPathsHolder);

                // If any of the symbols used in the predicate has changed
                for (Root l : notWantedLocalVariables) {
                    for (AccessExpression path : temporaryPathsHolder) {
                        isUnwanted |= path.isLocalVariable() && path.getRoot().getName().equals(l.getName());
                    }
                }

                for (AccessExpression path : temporaryPathsHolder) {
                    isAnonymous |= method.isInit() && path.getRoot().isThis(); // new C() ... C.<init> which calls B.<init> and A.<init> on "this" which is in fact anonymous
                }

                // If the predicate uses only allowed symbols (those that do not lose their meaning by changing scope)
                if (!isUnwanted) {
                    try {
                        predicate = predicate.replace(replacements).clone();
                        predicate.setScope(BytecodeUnlimitedRange.getInstance()); // <-- This is done to allow use of the predicate outside its original scope.

                        relevant.put(predicate, value);

                        // Handling mainly constructor (object still anonymous)
                        if (isAnonymous) {
                            callerScope.put(predicate, value);
                        }

                        // Write predicates over return through (this is useful when called from processMethodReturn)
                        if (PredicateUtil.isPredicateOverReturn(predicate)) {
                            calleeReturns.put(predicate.replace(returnValue, returnValueSpecific), value);
                        }
                    } catch (PredicateNotCloneableException e) {
                        // Silently ignore predicates that should never be cloned (those have either no meaning here: Assign, or little value: Tautology)
                    }
                }

                temporaryPathsHolder.clear();
            }

            // Usable predicates from callee with replaced occurences of formal parameters were collected
            // Select predicates that need to be updated (refer to an object that may have been modified by the callee: static, o.field, modified heap)

            Set<AccessExpression> aliases = new HashSet<AccessExpression>();
            Set<Predicate> toBeUpdated = new HashSet<Predicate>();

            // Maximal alias length
            //
            // method: m(A a) { a.x.y.z = ? }
            //
            // s.t = u.v.w
            // m(s.t)
            //
            // affected:
            //  s.t.x.y.z
            //  u.v.w.x.y.z
            //
            // |u.v.w.x.y.z| = |u.v.w| + |a.x.y.z| - 1
            int callerLength = 0;
            int calleeLength = 0;

            for (Predicate predicate : callerScope.getPredicates()) {
                predicate.addAccessExpressionsToSet(temporaryPathsHolder);

                for (AccessExpression ae : temporaryPathsHolder) {
                    callerLength = Math.max(callerLength, ae.getLength());
                }

                temporaryPathsHolder.clear();
            }

            for (Predicate predicate : getPredicates()) {
                predicate.addAccessExpressionsToSet(temporaryPathsHolder);

                for (AccessExpression ae : temporaryPathsHolder) {
                    calleeLength = Math.max(calleeLength, ae.getLength());
                }

                temporaryPathsHolder.clear();
            }

            // |x.y.z| = |x.y| + |a.z| - 1
            int aliasLength = callerLength + calleeLength - 1;

            for (Predicate predicate : callerScope.getPredicates()) {
                predicate.addAccessExpressionsToSet(temporaryPathsHolder);

                boolean canBeAffected = false;

                // Static
                for (AccessExpression path : temporaryPathsHolder) {
                    if (path.isStatic()) {
                        canBeAffected = true;

                        break;
                    }
                }

                // Reference arguments
                for (AccessExpression path : temporaryPathsHolder) {
                    for (int argIndex = 0, slotIndex = 0; argIndex < method.getNumberOfStackArguments() && !canBeAffected; ++argIndex) {
                        Expression expr = originalArguments[argIndex];

                        if (argTypes[argIndex] == Types.T_REFERENCE || argTypes[argIndex] == Types.T_ARRAY) {
                            // Could be null / access expression
                            if (expr instanceof AccessExpression) {
                                AccessExpression actualParameter = (AccessExpression) expr;

                                // Every predicate referring to an alias of the possibly modified parameter may be affected as well
                                abstraction.getSymbolTable().get(0).lookupAliases(path, aliasLength, aliases);

                                for (AccessExpression alias : aliases) {
                                    // reference-passed objects may have been affected by the method
                                    // except array lengths (those cannot change after passing a reference to the array)
                                    if (actualParameter.isProperPrefixOf(alias) && !(alias instanceof ArrayLengthRead && alias.getLength() == actualParameter.getLength() + 1)) {
                                        if (alias.getRoot().isThis() && sameObject) {
                                            // Constructors affect `this` only in scope of the class
                                            // No further subclass fields may be modified by the constructor
                                            // Therefore the initial valuation may stay intact after calling super constructor
                                            if (method.isInit() && alias.getLength() > 1) {
                                                ObjectFieldRead fr = (ObjectFieldRead) alias.get(2);

                                                ClassInfo cls = method.getClassInfo();

                                                while (cls != null && !canBeAffected) {
                                                    for (FieldInfo field : cls.getInstanceFields()) {
                                                        if (fr.getField().getName().equals(field.getName())) {
                                                            canBeAffected = true;

                                                            break;
                                                        }
                                                    }

                                                    cls = cls.getSuperClass();
                                                }
                                            } else {
                                                canBeAffected = true; // Not constructor
                                            }
                                        } else {
                                                canBeAffected = true; // Any non-this parameter
                                        }
                                    }
                                }

                                aliases.clear();
                            }
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

                    if (canBeAffected) {
                        break;
                    }
                }

                // Predicates are either updated (when they were possibly affected) or can be used for value inference.
                // We take all predicates that are not to-be-updated as possibly relevant (for simplicity). Actual determining predicates are selected later.

                if (canBeAffected) {
                    toBeUpdated.add(predicate);
                } else {
                    relevant.put(predicate, callerScope.get(predicate));
                }

                temporaryPathsHolder.clear();
            }

            // Use the relevant predicates to valuate predicates that need to be updated
            Map<Predicate, TruthValue> valuation = relevant.evaluatePredicates(after.getPC().getPosition(), toBeUpdated);

            for (Predicate predicate : valuation.keySet()) {
                TruthValue value = valuation.get(predicate);

                callerScope.put(predicate, value);
            }

            for (Predicate predicate : calleeReturns.keySet()) {
                callerScope.put(predicate, calleeReturns.get(predicate));
            }

            // Predicates may be reevaluated to UNKNOWN at return from method (especially aliasing predicates)
            // To restore precision we may update the aliasing predicates using the symbol table
            callerScope.improvePrecisionOfAliasingPredicates();
        }

        scopes.get(currentThreadID).pop();
    }

    @Override
    public void restore(Map<Integer, ? extends Scopes> scopes) {
        this.scopes.clear();
        for (Integer threadId : scopes.keySet()) {
            Scopes threadScopes = scopes.get(threadId);

            if (threadScopes instanceof PredicateValuationStack) {
                PredicateValuationStack threadPredicateValuationScopes = (PredicateValuationStack) threadScopes;

                this.scopes.put(threadId, threadPredicateValuationScopes.clone());
            } else {
                throw new RuntimeException("Invalid scopes type being restored!");
            }
        }
    }

    @Override
    public Map<Integer, PredicateValuationStack> memorize() {
        Map<Integer, PredicateValuationStack> scopesClone = new HashMap<Integer, PredicateValuationStack>();

        for (Integer threadId : scopes.keySet()) {
            scopesClone.put(threadId, scopes.get(threadId).clone());
        }

        return scopesClone;
    }

    public String toString(int pc) {
        return scopes.get(currentThreadID).count() > 0 ? scopes.get(currentThreadID).top().toString(pc) : "";
    }

    @Override
    public void reevaluate(int lastPC, int nextPC, AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression) {
        scopes.get(currentThreadID).top().reevaluate(lastPC, nextPC, affected, resolvedAffected, expression);
    }

    @Override
    public void dropAllPredicatesSharingSymbolsWith(AccessExpression expr) {
        scopes.get(currentThreadID).top().dropAllPredicatesSharingSymbolsWith(expr);
    }

    @Override
    public TruthValue evaluatePredicate(int lastPC, Predicate predicate) {
        return scopes.get(currentThreadID).top().evaluatePredicate(lastPC, predicate);
    }

    @Override
    public Map<Predicate, TruthValue> evaluatePredicates(int lastPC, Set<Predicate> predicates) {
        return scopes.get(currentThreadID).top().evaluatePredicates(lastPC, predicates);
    }

    @Override
    public Integer evaluateExpression(Expression expression) {
        return scopes.get(currentThreadID).top().evaluateExpression(expression);
    }

    @Override
    public int[] evaluateExpressionInRange(Expression expression, int lowerBound, int upperBound) {
        return scopes.get(currentThreadID).top().evaluateExpressionInRange(expression, lowerBound, upperBound);
    }

    @Override
    public int count() {
        return scopes.get(currentThreadID).count() > 0 ? scopes.get(currentThreadID).top().count() : 0;
    }

    @Override
    public int depth() {
        return scopes.get(currentThreadID).count();
    }

    @Override
    public boolean containsKey(Predicate predicate) {
        return scopes.get(currentThreadID).top().containsKey(predicate);
    }

    @Override
    public Set<Predicate> getPredicates() {
        return getPredicates(0);
    }

    public Set<Predicate> getPredicates(int i) {
        return scopes.get(currentThreadID).top(i).getPredicates();
    }

    @Override
    public MethodFramePredicateValuation get(int depth) {
        return get(currentThreadID, depth);
    }

    public MethodFramePredicateValuation get(int threadID, int depth) {
        return scopes.get(threadID).top(depth);
    }

    @Override
    public void addThread(ThreadInfo threadInfo) {
        MethodFramePredicateValuation startScope = null;

        // If this is not the creation of the main thread (which is created from nothing)
        // then there is another thread running which is currently inside `start`
        // therefore we shall steal the predicate valuation
        if (!scopes.isEmpty()) {
            startScope = scopes.get(currentThreadID).top();
        }

        PredicateValuationStack threadStack = new PredicateValuationStack();

        MethodInfo runMethod = threadInfo.getThreadObject().getClassInfo().getMethod("run()V", true);
        MethodFramePredicateValuation bottomScope = createDefaultScope(threadInfo, runMethod);

        threadStack.push("-- Dummy stop scope --", bottomScope);
        scopes.put(threadInfo.getId(), threadStack);

        if (startScope != null) {
            Set<Predicate> predicates = bottomScope.getPredicates();
            Map<Predicate, TruthValue> values = evaluatePredicates(0, predicates);

            for (Predicate predicate : predicates) {
                bottomScope.put(predicate, values.get(predicate));
            }
        }
    }

    @Override
    public void scheduleThread(ThreadInfo threadInfo) {
        scheduleThread(threadInfo.getId());
    }

    @Override
    public void scheduleThread(int threadID) {
        currentThreadID = threadID;
    }

    @Override
    public void print() {
        scopes.get(currentThreadID).print();
    }

}
