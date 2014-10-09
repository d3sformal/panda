package gov.nasa.jpf.abstraction.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.BytecodeRange;
import gov.nasa.jpf.abstraction.common.BytecodeUnlimitedRange;
import gov.nasa.jpf.abstraction.common.Comparison;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateUtil;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.smt.PredicateValueDeterminingInfo;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.Pair;

/**
 * A predicate valuation for a single scope
 */
public class MethodFramePredicateValuation implements PredicateValuation, Scope {
    private PredicateValuationMap valuations = new PredicateValuationMap();
    private HashMap<Predicate, Set<Predicate>> sharedSymbolCache = new HashMap<Predicate, Set<Predicate>>();
    private SMT smt;
    private int lastPC = -1;

    public MethodFramePredicateValuation(SMT smt) {
        this.smt = smt;
    }

    // not used right now (might be useful for multi-dimensional arrays)
    private void addArray(ElementInfo elementInfo, ThreadInfo threadInfo, AccessExpression name) {
        // we would need the lengths of all nested sub-arrays from MULTIANEWARRAY
        //put(Equals.create(DefaultArrayLengthRead.create(name), length));

        for (int i = 0; i < elementInfo.arrayLength(); ++i) {
            AccessExpression elementExpression = DefaultArrayElementRead.create(name, Constant.create(i));

            if (elementInfo.isReferenceArray()) {
                ElementInfo subElementInfo = threadInfo.getElementInfo(elementInfo.getReferenceElement(i));

                if (subElementInfo == null) {
                    put(Equals.create(elementExpression, NullExpression.create()), TruthValue.TRUE);
                } else {
                    addArray(subElementInfo, threadInfo, elementExpression);
                }
            } else {
                put(Equals.create(DefaultArrayElementRead.create(name, Constant.create(i)), Constant.create(0)), TruthValue.TRUE);
            }
        }
    }

    private void addObject(ElementInfo elementInfo, AccessExpression name) {
        ClassInfo classInfo = elementInfo.getClassInfo();

        while (classInfo != null) {
            for (FieldInfo field : classInfo.getInstanceFields()) {
                if (field.isReference()) {
                    put(Equals.create(DefaultObjectFieldRead.create(name, field.getName()), NullExpression.create()), TruthValue.TRUE);
                } else {
                    put(Equals.create(DefaultObjectFieldRead.create(name, field.getName()), Constant.create(0)), TruthValue.TRUE);
                }
            }

            classInfo = classInfo.getSuperClass();
        }
    }

    public void addObject(AnonymousObject object) {
        VM vm = VM.getVM();
        ThreadInfo threadInfo = vm.getCurrentThread();
        ElementInfo elementInfo = object.getReference().getElementInfo();

        if (object instanceof AnonymousArray) {
            // initialize all elements to default values
            // disabled now because it is too slow (it creates many predicates)
            // instead we use additional predicate over fresh (SMTInfoCollector)
            // we do not need it because there is very little support for multi-dimensional arrays
            //addArray(elementInfo, threadInfo, object);
        } else {
            // initialize all fields across all super classes to default values
            addObject(elementInfo, object);
        }
    }

    @Override
    public MethodFramePredicateValuation clone() {
        MethodFramePredicateValuation clone = new MethodFramePredicateValuation(smt);

        clone.valuations = valuations.clone();
        clone.lastPC = lastPC;

        /**
         * No need to memorize
         */
        clone.sharedSymbolCache = new HashMap<Predicate, Set<Predicate>>();

        return clone;
    }

    /**
     * @param candidatePaths a space to put paths to. Optimized to avoid new allocations at each invocation
     * @param prefixes a space to put prefixes to. Optimized to avoid new allocations at each invocation
     */
    private static boolean shareSymbols(Predicate candidate, Set<AccessExpression> paths, Set<AccessExpression> candidatePaths, Set<AccessExpression> prefixes) {
        candidatePaths.clear();
        candidate.addAccessExpressionsToSet(candidatePaths);

        for (AccessExpression path : paths) {
            // check shared access expressions between the candidate and the input predicate
            // stop at first match
            for (AccessExpression candidatePath : candidatePaths) {
                prefixes.clear();
                candidatePath.addAllPrefixesToSet(prefixes);

                for (AccessExpression candidateSubPath : prefixes) {
                    if (candidateSubPath.isSimilarToPrefixOf(path)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may directly determine the value of this predicate
     */
    private static void selectPredicatesSharingSymbols(Predicate predicate, Set<Predicate> universe, Set<Predicate> outPredicates) {
        Set<AccessExpression> paths = new HashSet<AccessExpression>();
        Set<AccessExpression> candidatePaths = new HashSet<AccessExpression>();
        Set<AccessExpression> prefixes = new HashSet<AccessExpression>();

        predicate.addAccessExpressionsToSet(paths);

        // filter all determinants from all the candidates
        for (Predicate candidate : universe) {
            if (shareSymbols(candidate, paths, candidatePaths, prefixes)) {
                outPredicates.add(candidate);
            }
        }
    }

    /**
     * Finds a transitive closure of all predicates that may infer the value of this one.
     *
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may determine the value of this predicate
     */
    private Set<Predicate> computeDeterminantClosure(Predicate predicate, Set<Predicate> universe) {
        return computeSharedSymbolsClosure(predicate, universe);
    }

    private Set<Predicate> computeAffectedClosure(Predicate predicate, Set<Predicate> universe) {
        return computeSharedSymbolsClosure(predicate, universe);
    }

    private Set<Predicate> computeSharedSymbolsClosure(Predicate predicate, Set<Predicate> universe) {
        if (!sharedSymbolCache.containsKey(predicate)) {
            // Currently discovered elements of the closure
            Set<Predicate> cur;

            // This set will eventually contain the entire closure
            Set<Predicate> all = new HashSet<Predicate>();

            // Elements of the closure that have not yet been processed to obtain other elements
            Set<Predicate> open = new HashSet<Predicate>();

            selectPredicatesSharingSymbols(predicate, universe, open);
            all.addAll(open);

            while (!open.isEmpty()) {
                cur = new HashSet<Predicate>();

                for (Predicate p : open) {
                    selectPredicatesSharingSymbols(p, universe, cur);
                }

                open.clear();

                for (Predicate p : cur) {
                    // Continue exploring only new elements (avoid redundant computations)
                    if (!all.contains(p)) {
                        open.add(p);
                    }
                    all.add(p);
                }
            }

            sharedSymbolCache.put(predicate, all);
        }

        return sharedSymbolCache.get(predicate);
    }

    // Determine inconsistency with all predicates together, pairwise consistency is not enough
    @Override
    public Set<Predicate> getPredicatesInconsistentWith(Predicate assumption, TruthValue value) {
        Set<Predicate> affected = computeAffectedClosure(assumption, valuations.keySet());
        List<Predicate> formulas = new ArrayList<Predicate>(1);

        boolean[] satisfiable;

        Predicate formula = Tautology.create();

        switch (value) {
            case TRUE:
                formula = Conjunction.create(formula, assumption);
                break;
            case FALSE:
                formula = Conjunction.create(formula, Negation.create(assumption));
                break;

            case UNKNOWN:
            default:
        }

        for (Predicate predicate : affected) {
            switch (get(predicate)) {
                case TRUE:
                    formula = Conjunction.create(formula, predicate);
                    break;
                case FALSE:
                    formula = Conjunction.create(formula, Negation.create(predicate));
                    break;

                case UNKNOWN:
                default:
            }
        }

        formulas.add(formula);

        satisfiable = smt.isSatisfiable(formulas);

        if (satisfiable[0]) {
            return Collections.emptySet();
        } else {
            return affected;
        }
    }

    /**
     * Force (create/overwrite) valuation of the given predicate to the given value
     *
     * precondition: the value must be consistent (it can only improve precision)
     */
    @Override
    public void force(Predicate predicate, TruthValue value) {
        put(predicate, value);

        if (PandaConfig.getInstance().reevaluatePredicatesAfterBranching()) {
            Set<Predicate> affected = computeAffectedClosure(predicate, valuations.keySet());

            Map<Predicate, PredicateValueDeterminingInfo> predicateDeterminingInfos = new HashMap<Predicate, PredicateValueDeterminingInfo>();

            // Update predicates
            // One-shot reevaluation is enough (no need to repeat until fixpoint)
            //   1) we can only improve precision, not change from TRUE to FALSE or vice versa
            //   2) a predicate can be valuated to UNKNOWN at first, but if it should be changed to TRUE (without loss of generality) in the second step, then we would not assign it UNKNOWN in the first step in the first place
            for (Predicate affectedPredicate : affected) {

                // Improve precision of only imprecise predicates
                if (get(affectedPredicate) != TruthValue.UNKNOWN) continue;

                Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();

                Predicate positiveWeakestPrecondition = affectedPredicate;
                Predicate negativeWeakestPrecondition = Negation.create(affectedPredicate);

                for (Predicate determinantCandidate : computeDeterminantClosure(positiveWeakestPrecondition, valuations.keySet())) {
                    // When affectedPredicate = determinantCandidate: we know that
                    // a) the value is either UNKNOWN (the determinant will be left out in the end)
                    // b) the value is consistent with the forced predicate's value, therefore we can use it
                    determinants.put(determinantCandidate, get(determinantCandidate));
                }
                // Symmetric for negativeWeakestPrecondition ... no need to do it twice now (the symbols should be the same)

                predicateDeterminingInfos.put(affectedPredicate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
            }

            putAll(smt.valuatePredicates(predicateDeterminingInfos));
        }
    }

    @Override
    public void put(Predicate predicate, TruthValue value) {
        // Change of the set of predicates -> need to recompute determinant sets
        if (!containsKey(predicate)) {
            // Least effort
            // It is sufficient to "invalidate" the cache, let it be recomputed next time an element is requested
            //sharedSymbolCache.clear();

            // Most effort
            // An alternative approach: extend all cached sets if they should also contain the newly added predicate
            Set<AccessExpression> paths = new HashSet<AccessExpression>();
            Set<AccessExpression> candidatePaths = new HashSet<AccessExpression>();
            Set<AccessExpression> prefixes = new HashSet<AccessExpression>();

            // Update cache of symbols (access expressions) between predicates
            for (Predicate p : sharedSymbolCache.keySet()) {
                paths.clear();
                p.addAccessExpressionsToSet(paths);
                if (shareSymbols(predicate, paths, candidatePaths, prefixes)) {
                    sharedSymbolCache.get(p).add(predicate);
                } else {
                    boolean shouldBeAdded = false;
                    for (Predicate q : sharedSymbolCache.get(p)) {
                        paths.clear();
                        q.addAccessExpressionsToSet(paths);
                        if (shareSymbols(predicate, paths, candidatePaths, prefixes)) {
                            shouldBeAdded = true;
                            break;
                        }
                    }
                    if (shouldBeAdded) {
                        sharedSymbolCache.get(p).add(predicate);
                    }
                }
            }
        }

        if (valuations.containsKey(predicate)) {
            BytecodeRange scopeNew = predicate.getScope();

            for (Predicate p : valuations.keySet()) {
                if (p.equals(predicate)) {
                    BytecodeRange scopeOld = p.getScope();

                    if (scopeOld instanceof BytecodeUnlimitedRange) {
                        p.setScope(scopeNew);
                    } else if (!(scopeNew instanceof BytecodeUnlimitedRange)) {
                        p.setScope(scopeOld.merge(scopeNew));
                    }

                    break;
                }
            }
        }

        valuations.put(predicate, value);
    }

    /**
     * Same as the above (for more predicates at once)
     */
    @Override
    public void putAll(Map<Predicate, TruthValue> values) {
        for (Predicate predicate : values.keySet()) {
            put(predicate, values.get(predicate));
        }
    }

    @Override
    public void remove(Predicate predicate) {
        valuations.remove(predicate);
    }

    @Override
    public TruthValue get(Predicate predicate) {
        return valuations.get(predicate);
    }

    public String toString(int pc) {
        StringBuilder ret = new StringBuilder();

        Set<Predicate> sorted = new TreeSet<Predicate>(new Comparator<Predicate>() {
            @Override
            public int compare(Predicate p1, Predicate p2) {
                return p1.toString(Notation.DOT_NOTATION).compareTo(p2.toString(Notation.DOT_NOTATION));
            }
        });

        sorted.addAll(valuations.keySet());

        for (Predicate p : sorted) {
            if (p.isInScope(pc)) {
                String predicate = p.toString(Notation.DOT_NOTATION);

                ret.append(predicate);
                ret.append(" : ");
                ret.append(get(p));
                ret.append("\n");
            }
        }

        return ret.toString();
    }

    private void collectDeterminants(int lastPC, Predicate predicate, Map<Predicate, TruthValue> determinants, Map<AccessExpression, Predicate> equalities, Map<AccessExpression, Set<Predicate>> inequalities) {
        for (Predicate determinant : computeDeterminantClosure(predicate, valuations.keySet())) {
            if (determinant.isInScope(lastPC)) {
                if (PredicateUtil.determinesExactConcreteValueOfAccessExpression(determinant, valuations)) {
                    AccessExpression expression = PredicateUtil.getAccessExpression(determinant);

                    equalities.put(expression, determinant);
                } else if (PredicateUtil.forbidsExactConcreteValueOfAccessExpression(determinant, valuations)) {
                    AccessExpression expression = PredicateUtil.getAccessExpression(determinant);

                    if (!inequalities.containsKey(expression)) {
                        inequalities.put(expression, new HashSet<Predicate>());
                    }

                    inequalities.get(expression).add(determinant);
                } else {
                    determinants.put(determinant, get(determinant));
                }
            }
        }

        for (AccessExpression expr : inequalities.keySet()) {
            if (!equalities.containsKey(expr)) {
                for (Predicate inequality : inequalities.get(expr)) {
                    determinants.put(inequality, valuations.get(inequality));
                }
            }
        }

        for (Predicate equality : equalities.values()) {
            determinants.put(equality, valuations.get(equality));
        }

        equalities.clear();
        inequalities.clear();
    }

    @Override
    public void reevaluate(int lastPC, int nextPC, AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression) {
        Map<Predicate, PredicateValueDeterminingInfo> predicates = new HashMap<Predicate, PredicateValueDeterminingInfo>();

        Map<AccessExpression, Predicate> equalities = new HashMap<AccessExpression, Predicate>();
        Map<AccessExpression, Set<Predicate>> inequalities = new HashMap<AccessExpression, Set<Predicate>>();

        Set<AccessExpression> paths = new HashSet<AccessExpression>();

        for (Predicate predicate : valuations.keySet()) {
            boolean affects = false;

            /**
             * Affected may contain completely different paths:
             *
             * assume a == b.c == d.e.f[ ? ]
             * then
             *
             * a.x := 3
             *
             * causes
             *
             * a.x, b.c.x, d.e.f[ ? ].x
             *
             * be affected paths, hence
             *
             * a.x = 3
             * b.c.x + 2 = g + h
             * d.e.f[0].x / 3 = 4
             * d.e.f[1].x / 3 = 4
             * d.e.f[k + l * m].x / 3 = 4
             *
             * are affected predicates
             */

            predicate.addAccessExpressionsToSet(paths);

            // Do not cycle through the collection if the other is empty
            if (!paths.isEmpty()) {
                for (AccessExpression path1 : resolvedAffected) {
                    for (AccessExpression path2 : paths) {
                        affects = affects || path1.isSimilarToPrefixOf(path2);
                    }
                }
            }

            paths.clear();

            /**
             * If the predicate may be affected, compute preconditions and determinants and schedule the predicate for reevaluation
             */
            if (affects) {
                if (predicate.isInScope(nextPC)) {
                    Predicate positiveWeakestPrecondition = predicate;
                    Predicate negativeWeakestPrecondition = Negation.create(predicate);

                    if (expression != null) {
                        positiveWeakestPrecondition = UpdatedPredicate.create(positiveWeakestPrecondition, affected, expression);
                        negativeWeakestPrecondition = UpdatedPredicate.create(negativeWeakestPrecondition, affected, expression);
                    }

                    Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
                    Map<Predicate, TruthValue> determinantsOrig = new HashMap<Predicate, TruthValue>();

                    collectDeterminants(lastPC, positiveWeakestPrecondition, determinants, equalities, inequalities);
                    // This is not necessary
                    //collectDeterminants(lastPC, negativeWeakestPrecondition, determinants, equalities, inequalities);

                    predicates.put(predicate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
                } else {
                    /**
                     * Havoc values of predicates that are not in scope
                     * (We do not want a predicate to keep its old value when it returns to scope after being out of it (and thus it was not maintained properly))
                     *
                     * This should not be necessary
                     * It would, however, be hard to detect when old values are used, even though the predicate ran out of scope before getting into the scope again
                     */
                    valuations.put(predicate, TruthValue.UNKNOWN);
                }
            }
        }

        /**
         * Save a void call to SMT
         */
        if (predicates.isEmpty()) return;

        /**
         * Collective reevaluation
         */
        Map<Predicate, TruthValue> newValuations = smt.valuatePredicates(predicates);

        putAll(newValuations);

        /**
         * Aliasing cannot be changed when only variables of primitive data types are affected
         * It is not necessary to update predicates that express aliasing
         */
        MethodFrameSymbolTable sym = PredicateAbstraction.getInstance().getSymbolTable().get(0);
        if (!sym.isPrimitive(affected)) {
            improvePrecisionOfAliasingPredicates();
        }
    }

    private static boolean isAliasingPredicate(Predicate p, MethodFrameSymbolTable sym) {
        while (p instanceof Negation) {
            p = ((Negation) p).predicate;
        }

        if (p instanceof Equals) {
            Expression a = ((Equals) p).a;
            Expression b = ((Equals) p).b;

            if (a instanceof AccessExpression && b instanceof AccessExpression) {
                if (sym.isObject((AccessExpression) a) && sym.isObject((AccessExpression) b)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static AccessExpression getFirstExpression(Predicate p) {
        while (p instanceof Negation) {
            p = ((Negation) p).predicate;
        }

        return (AccessExpression) ((Equals) p).a;
    }

    private static AccessExpression getSecondExpression(Predicate p) {
        while (p instanceof Negation) {
            p = ((Negation) p).predicate;
        }

        return (AccessExpression) ((Equals) p).b;
    }

    private static boolean areEquivalentSingletonSets(Set<UniverseIdentifier> a, Set<UniverseIdentifier> b) {
        return a.size() == 1 && a.equals(b);
    }

    private static boolean areOverlappingSets(Set<UniverseIdentifier> a, Set<UniverseIdentifier> b) {
        if (a.size() > b.size()) {
            Set<UniverseIdentifier> swap = a;

            a = b;
            b = swap;
        }

        for (UniverseIdentifier id : a) {
            if (b.contains(id)) {
                return true;
            }
        }

        return false;
    }

    public void improvePrecisionOfAliasingPredicates() {
        MethodFrameSymbolTable sym = PredicateAbstraction.getInstance().getSymbolTable().get(0);
        Map<Predicate, TruthValue> improved = new HashMap<Predicate, TruthValue>();

        for (Predicate p : getPredicates()) {
            if (isAliasingPredicate(p, sym) && get(p) == TruthValue.UNKNOWN) {
                TruthValue aliased = p instanceof Equals ? TruthValue.TRUE : TruthValue.FALSE;

                AccessExpression a = getFirstExpression(p);
                AccessExpression b = getSecondExpression(p);

                Set<UniverseIdentifier> valuesA = new HashSet<UniverseIdentifier>();
                Set<UniverseIdentifier> valuesB = new HashSet<UniverseIdentifier>();

                sym.lookupValues(a, valuesA);
                sym.lookupValues(b, valuesB);

                if (areEquivalentSingletonSets(valuesA, valuesB)) {
                    improved.put(p, aliased);
                } else if (!areOverlappingSets(valuesA, valuesB)) {
                    improved.put(p, TruthValue.neg(aliased));
                }
            }
        }

        putAll(improved);
    }

    @Override
    public void dropAllPredicatesSharingSymbolsWith(AccessExpression expr) {
        Set<AccessExpression> paths = new HashSet<AccessExpression>();
        Set<Predicate> toBeRemoved = new HashSet<Predicate>();

        for (Predicate p : getPredicates()) {
            p.addAccessExpressionsToSet(paths);

            for (AccessExpression path : paths) {
                if (expr.isPrefixOf(path)) {
                    toBeRemoved.add(p);
                }
            }

            paths.clear();
        }

        for (Predicate p : toBeRemoved) {
            remove(p);
        }

        // Change of the set of predicates -> need to recompute determinant sets
        if (!toBeRemoved.isEmpty()) {
            sharedSymbolCache.clear();
        }
    }

    public void evaluateJustInScopePredicates(int pc) {
        Set<Predicate> arrivedToScope = new HashSet<Predicate>();

        for (Predicate p : getPredicates()) {
            if (!p.isInScope(lastPC) && p.isInScope(pc)) { // Just arrived to scope
                if (get(p) == TruthValue.UNKNOWN) { // TRUE and FALSE mean the predicate has just been valuated
                    arrivedToScope.add(p);
                }
            }
        }

        // Force not using the old UNKNOWN values
        for (Predicate p : arrivedToScope) {
            remove(p);
        }

        Map<Predicate, TruthValue> valuations = evaluatePredicates(lastPC, arrivedToScope);

        // Add new values
        for (Predicate p : valuations.keySet()) {
            put(p, valuations.get(p));
        }

        lastPC = pc;
    }

    /**
     * Evaluate a single predicate regardless of a statement
     *
     * Used to detect tautologies in the initial phase.
     * Used to valuate branching conditions - a (possibly) new predicate whose value depends solely on current predicate valuation.
     */
    @Override
    public TruthValue evaluatePredicate(int lastPC, Predicate predicate) {
        Set<Predicate> predicates = new HashSet<Predicate>();

        predicates.add(predicate);

        return evaluatePredicates(lastPC, predicates).get(predicate);
    }

    /**
     * Evaluate predicates regardless of a statement
     *
     * Batch variant of evaluatePredicate
     */
    @Override
    public Map<Predicate, TruthValue> evaluatePredicates(int lastPC, Set<Predicate> predicates) {
        if (predicates.isEmpty()) return Collections.emptyMap();

        Map<Predicate, PredicateValueDeterminingInfo> input = new HashMap<Predicate, PredicateValueDeterminingInfo>();
        Set<Predicate> known = new HashSet<Predicate>();
        Map<AccessExpression, Predicate> equalities = new HashMap<AccessExpression, Predicate>();
        Map<AccessExpression, Set<Predicate>> inequalities = new HashMap<AccessExpression, Set<Predicate>>();

        // This takes long
        for (Predicate predicate : predicates) {
            // Skip predicates whose value is directly known
            if (containsKey(predicate)) {
                known.add(predicate);
            } else {
                Predicate positiveWeakestPrecondition = predicate;
                Predicate negativeWeakestPrecondition = Negation.create(predicate);

                Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();

                collectDeterminants(lastPC, positiveWeakestPrecondition, determinants, equalities, inequalities);
                // This is not necessary
                //collectDeterminants(lastPC, negativeWeakestPrecondition, determinants, equalities, inequalities);

                input.put(predicate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
            }
        }

        Map<Predicate, TruthValue> ret;

        if (input.isEmpty()) {
            ret = new HashMap<Predicate, TruthValue>();
        } else {
            ret = smt.valuatePredicates(input);
        }

        for (Predicate p : known) {
            ret.put(p, get(p));
        }

        return ret;
    }

    @Override
    public int count() {
        return valuations.keySet().size();
    }

    @Override
    public boolean containsKey(Predicate predicate) {
        return valuations.containsKey(predicate);
    }

    @Override
    public Set<Predicate> getPredicates() {
        return valuations.keySet();
    }

    @Override
    public Integer evaluateExpression(Expression expression) {
        List<Pair<Predicate, TruthValue>> determinants = new LinkedList<Pair<Predicate, TruthValue>>();

        // An auxiliary predicate used to get all determining predicates
        // Determinants are defined only for predicates, not expressions
        Predicate determinantSelector = Equals.create(expression, Constant.create(0));

        // Collect determinant valuations
        for (Predicate predicate : computeDeterminantClosure(determinantSelector, getPredicates())) {
            determinants.add(new Pair<Predicate, TruthValue>(predicate, get(predicate)));
        }

        // Query any model
        Integer model1 = smt.getModel(expression, determinants);

        if (model1 != null) {
            // Forbid the first model, look for others
            determinants.add(new Pair<Predicate, TruthValue>(Equals.create(expression, Constant.create(model1)), TruthValue.FALSE));

            Integer model2 = smt.getModel(expression, determinants);

            // Only if there is a single satisfying value for the expression
            if (model2 == null) {
                return model1;
            }
        }

        return null;
    }

    /**
     * returns an array of all distinct admissible values of the given expression in the range from lowerBound (inclusive) to upperBound (exclusive)
     * the current state is taken into account when ruling out inadmissible values
     *
     * uses SMT to compute values of the expression
     */
    @Override
    public int[] evaluateExpressionInRange(Expression expression, int lowerBound, int upperBound) {
        List<Integer> models = new LinkedList<Integer>();
        List<Pair<Predicate, TruthValue>> determinants = new LinkedList<Pair<Predicate, TruthValue>>();

        // An auxiliary predicate used to get all determining predicates
        // Determinants are defined only for predicates, not expressions
        Predicate determinantSelector = Equals.create(expression, Constant.create(0));

        // Collect determinant valuations
        for (Predicate predicate : computeDeterminantClosure(determinantSelector, getPredicates())) {
            determinants.add(new Pair<Predicate, TruthValue>(predicate, get(predicate)));
        }

        determinants.add(new Pair<Predicate, TruthValue>(LessThan.create(expression, Constant.create(lowerBound)), TruthValue.FALSE)); // inclusive
        determinants.add(new Pair<Predicate, TruthValue>(LessThan.create(expression, Constant.create(upperBound)), TruthValue.TRUE)); // exlusive

        // Query any model
        Integer model = smt.getModel(expression, determinants);

        while (model != null) {
            models.add(model);

            determinants.add(new Pair<Predicate, TruthValue>(Equals.create(expression, Constant.create(model)), TruthValue.FALSE));

            model = smt.getModel(expression, determinants);
        }

        int i = 0;
        int[] ret = new int[models.size()];

        for (Integer m : models) {
            ret[i] = m;
            ++i;
        }

        return ret;
    }

    public void addAccessExpressionsToSet(Set<AccessExpression> exprs) {
        for (Predicate p : valuations.keySet()) {
            p.addAccessExpressionsToSet(exprs);
        }
    }

    /**
     * Returns a part of an arbitrary concrete state (restricted to given expressions) satisfying the current abstract state
     * If there are multiple models then it returns an arbitrary concrete state (from the set of possible models)
     *
     * @param exprArray is a list of expressions whose concrete value should be derived
     */
    public int[] getConcreteState(AccessExpression[] exprArray, int pc) {
        Predicate state = Tautology.create();

        for (Predicate p : valuations.keySet()) {
            if (p.isInScope(pc)) {
                switch (valuations.get(p)) {
                    case TRUE:
                        state = Conjunction.create(state, p);
                        break;
                    case FALSE:
                        state = Conjunction.create(state, Negation.create(p));
                        break;
                }
            }
        }

        return getModels(state, exprArray);
    }

    public int[] getModels(Predicate state, AccessExpression[] exprArray) {
        return smt.getModels(state, exprArray);
    }
}
