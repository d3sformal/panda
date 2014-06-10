package gov.nasa.jpf.abstraction.common;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;

/**
 * Context is a container holding predicates that are targeted at a specific runtime scope (static, object, method)
 *
 * @see gov.nasa.jpf.abstraction.common.StaticPredicateContext for a container of predicates over static fields
 * @see gov.nasa.jpf.abstraction.common.ObjectPredicateContext for a container of predicates over static fields, instance fields
 * @see gov.nasa.jpf.abstraction.common.MethodPredicateContext for a container of predicates over static fields, instance fields, local variables (including method parameters)
 */
public abstract class PredicateContext implements PredicatesComponentVisitable {
    public List<Predicate> predicates;

    public PredicateContext(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public String toString() {
        String ret = "";

        for (Predicate p : predicates) {
            ret += p.toString() + "\n";
        }

        return ret;
    }
}