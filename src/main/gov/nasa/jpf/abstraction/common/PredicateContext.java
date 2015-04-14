package gov.nasa.jpf.abstraction.common;

import java.util.List;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.BytecodeRange;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.state.PredicateValuationMap;
import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * Context is a container holding predicates that are targeted at a specific runtime scope (static, object, method)
 *
 * @see gov.nasa.jpf.abstraction.common.StaticPredicateContext for a container of predicates over static fields
 * @see gov.nasa.jpf.abstraction.common.ObjectPredicateContext for a container of predicates over static fields, instance fields
 * @see gov.nasa.jpf.abstraction.common.MethodPredicateContext for a container of predicates over static fields, instance fields, local variables (including method parameters)
 */
public abstract class PredicateContext implements PredicatesComponentVisitable {
    protected PredicateValuationMap predicates = new PredicateValuationMap();

    public PredicateContext(List<Predicate> predicates) {
        for (Predicate p : predicates) {
            put(p, TruthValue.UNKNOWN);
        }
    }

    @Override
    public String toString() {
        String ret = "";

        for (Predicate p : predicates.keySet()) {
            ret += p.toString() + "\n";
        }

        return ret;
    }

    public Set<Predicate> getPredicates() {
        return predicates.keySet();
    }

    public TruthValue get(Predicate p) {
        return predicates.get(p);
    }

    public TruthValue put(Predicate p, TruthValue v) {
        return predicates.put(p, v);
    }

    public boolean contains(Predicate p) {
        return predicates.containsKey(p);
    }
}
