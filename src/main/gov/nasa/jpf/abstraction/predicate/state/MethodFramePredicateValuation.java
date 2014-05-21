package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.abstraction.util.Pair;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.smt.PredicateValueDeterminingInfo;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;

/**
 * A predicate valuation for a single scope
 */
public class MethodFramePredicateValuation implements PredicateValuation, Scope {
    private PredicateValuationMap valuations = new PredicateValuationMap();
    private HashMap<Predicate, Set<Predicate>> sharedSymbolCache = new HashMap<Predicate, Set<Predicate>>();
    private SMT smt;

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

        /**
         * No need to memorize
         */
        clone.sharedSymbolCache = new HashMap<Predicate, Set<Predicate>>();

        return clone;
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
            boolean alreadyAdded = false;

            candidate.addAccessExpressionsToSet(candidatePaths);

            for (AccessExpression path : paths) {
                // check shared access expressions between the candidate and the input predicate
                // stop at first match
                for (AccessExpression candidatePath : candidatePaths) {
                    candidatePath.addAllPrefixesToSet(prefixes);

                    for (AccessExpression candidateSubPath : prefixes) {
                        if (candidateSubPath.isSimilarToPrefixOf(path)) {
                            outPredicates.add(candidate);

                            alreadyAdded = true;
                            break;
                        }
                    }

                    prefixes.clear();

                    if (alreadyAdded) {
                        break;
                    }
                }

                if (alreadyAdded) {
                    break;
                }
            }

            candidatePaths.clear();
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
            Set<Predicate> cur;
            Set<Predicate> ret = new HashSet<Predicate>();

            selectPredicatesSharingSymbols(predicate, universe, ret);

            int prevSize = 0;

            while (prevSize != ret.size()) {
                prevSize = ret.size();

                cur = new HashSet<Predicate>();

                for (Predicate p : ret) {
                    selectPredicatesSharingSymbols(p, universe, cur);
                }

                // each `p` in `ret` is contained in `selectDet(p)` and therefore also in `cur`
                // thus the following statement avoids unnecessary merge of the sets
                ret = cur;
            }

            sharedSymbolCache.put(predicate, ret);
        }

        return sharedSymbolCache.get(predicate);
    }

    @Override
    public Set<Predicate> getPredicatesInconsistentWith(Predicate assumption, TruthValue value) {
        Set<Predicate> affected = computeAffectedClosure(assumption, valuations.keySet());

        Set<Predicate> inconsistent = new HashSet<Predicate>();
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

        List<Predicate> predicates = new ArrayList<Predicate>(affected.size());
        List<Predicate> formulas = new ArrayList<Predicate>(affected.size());

        for (Predicate predicate : affected) {
            switch (get(predicate)) {
                case TRUE:
                    predicates.add(predicate);
                    formulas.add(Conjunction.create(formula, predicate));
                    break;
                case FALSE:
                    predicates.add(predicate);
                    formulas.add(Conjunction.create(formula, Negation.create(predicate)));
                    break;

                case UNKNOWN:
                default:
            }
        }

        satisfiable = smt.isSatisfiable(formulas);

        for (int i = 0; i < predicates.size(); ++i) {
            if (!satisfiable[i]) {
                inconsistent.add(predicates.get(i));
            }
        }

        return inconsistent;
    }

    /**
     * Force (create/overwrite) valuation of the given predicate to the given value
     *
     * precondition: the value must be consistent (it can only improve precision)
     */
    @Override
    public void force(Predicate predicate, TruthValue value) {
        Config config = VM.getVM().getJPF().getConfig();
        String key = "abstract.branch.reevaluate_predicates";

        put(predicate, value);

        if (config.containsKey(key) && config.getBoolean(key)) {
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
            sharedSymbolCache.clear();
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

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        Set<Predicate> sorted = new TreeSet<Predicate>(new Comparator<Predicate>() {
            @Override
            public int compare(Predicate p1, Predicate p2) {
                return p1.toString(Notation.DOT_NOTATION).compareTo(p2.toString(Notation.DOT_NOTATION));
            }
        });

        sorted.addAll(valuations.keySet());

        for (Predicate p : sorted) {
            String predicate = p.toString(Notation.DOT_NOTATION);

            ret.append(predicate);
            ret.append(" : ");
            ret.append(get(p));
            ret.append("\n");
        }

        return ret.toString();
    }

    @Override
    public void reevaluate(AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression) {
        Map<Predicate, PredicateValueDeterminingInfo> predicates = new HashMap<Predicate, PredicateValueDeterminingInfo>();

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
                Predicate positiveWeakestPrecondition = predicate;
                Predicate negativeWeakestPrecondition = Negation.create(predicate);

                if (expression != null) {
                    positiveWeakestPrecondition = UpdatedPredicate.create(positiveWeakestPrecondition, affected, expression);
                    negativeWeakestPrecondition = UpdatedPredicate.create(negativeWeakestPrecondition, affected, expression);
                }

                Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();

                for (Predicate determinant : computeDeterminantClosure(positiveWeakestPrecondition, valuations.keySet())) {
                    determinants.put(determinant, get(determinant));
                }
                for (Predicate determinant : computeDeterminantClosure(negativeWeakestPrecondition, valuations.keySet())) {
                    determinants.put(determinant, get(determinant));
                }

                predicates.put(predicate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
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

        improvePrecisionOfAliasingPredicates();
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

    private static AccessExpression getLeftHand(Predicate p) {
        while (p instanceof Negation) {
            p = ((Negation) p).predicate;
        }

        return (AccessExpression) ((Equals) p).a;
    }

    private static AccessExpression getRightHand(Predicate p) {
        while (p instanceof Negation) {
            p = ((Negation) p).predicate;
        }

        return (AccessExpression) ((Equals) p).b;
    }

    private static boolean areSingleton(Set<UniverseIdentifier> a, Set<UniverseIdentifier> b) {
        return a.size() == 1 && a.equals(b);
    }

    private static boolean overlap(Set<UniverseIdentifier> a, Set<UniverseIdentifier> b) {
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
        MethodFrameSymbolTable sym = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0);
        Map<Predicate, TruthValue> improved = new HashMap<Predicate, TruthValue>();

        for (Predicate p : getPredicates()) {
            if (isAliasingPredicate(p, sym) && get(p) == TruthValue.UNKNOWN) {
                TruthValue aliased = p instanceof Equals ? TruthValue.TRUE : TruthValue.FALSE;

                AccessExpression a = getLeftHand(p);
                AccessExpression b = getRightHand(p);

                Set<UniverseIdentifier> valuesA = new HashSet<UniverseIdentifier>();
                Set<UniverseIdentifier> valuesB = new HashSet<UniverseIdentifier>();

                sym.lookupValues(a, valuesA);
                sym.lookupValues(b, valuesB);

                if (areSingleton(valuesA, valuesB)) {
                    improved.put(p, aliased);
                } else if (!overlap(valuesA, valuesB)) {
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

    /**
     * Evaluate a single predicate regardless of a statement
     *
     * Used to detect tautologies in the initial phase.
     * Used to valuate branching conditions - a (possibly) new predicate whose value depends solely on current predicate valuation.
     */
    @Override
    public TruthValue evaluatePredicate(Predicate predicate) {
        Set<Predicate> predicates = new HashSet<Predicate>();

        predicates.add(predicate);

        return evaluatePredicates(predicates).get(predicate);
    }

    /**
     * Evaluate predicates regardless of a statement
     *
     * Batch variant of evaluatePredicate
     */
    @Override
    public Map<Predicate, TruthValue> evaluatePredicates(Set<Predicate> predicates) {
        if (predicates.isEmpty()) return Collections.emptyMap();

        Map<Predicate, PredicateValueDeterminingInfo> input = new HashMap<Predicate, PredicateValueDeterminingInfo>();
        Set<Predicate> known = new HashSet<Predicate>();

        // This takes long
        for (Predicate predicate : predicates) {
            // Skip predicates whose value is directly known
            if (containsKey(predicate)) {
                known.add(predicate);
            } else {
                Predicate positiveWeakestPrecondition = predicate;
                Predicate negativeWeakestPrecondition = Negation.create(predicate);

                Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();

                for (Predicate determinant : computeDeterminantClosure(positiveWeakestPrecondition, valuations.keySet())) {
                    determinants.put(determinant, get(determinant));
                }
                for (Predicate determinant : computeDeterminantClosure(negativeWeakestPrecondition, valuations.keySet())) {
                    determinants.put(determinant, get(determinant));
                }

                input.put(predicate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
            }
        }

        Map<Predicate, TruthValue> ret = smt.valuatePredicates(input);

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
}
