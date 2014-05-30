package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

import java.util.List;

/**
 * A container of all input predicates (read from a file) divided into individual contexts
 *
 * @see gov.nasa.jpf.abstraction.common.Context
 */
public class Predicates implements PredicatesComponentVisitable {
    public List<Context> contexts;

    public Predicates(List<Context> contexts) {
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
