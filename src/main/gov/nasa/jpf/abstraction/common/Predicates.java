package gov.nasa.jpf.abstraction.common;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

/**
 * A container of all input predicates (read from a file) divided into individual contexts
 *
 * @see gov.nasa.jpf.abstraction.common.PredicateContext
 */
public class Predicates implements PredicatesComponentVisitable {
    public List<PredicateContext> contexts;

    public Predicates() {
        this(new LinkedList<PredicateContext>());
    }

    public Predicates(List<PredicateContext> contexts) {
        this.contexts = contexts;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return Notation.convertToString(this);
    }
}
