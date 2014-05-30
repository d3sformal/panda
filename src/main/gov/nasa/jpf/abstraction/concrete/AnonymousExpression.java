package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;

/**
 * Interface of all Objects / Arrays obtained by allocation (or duplicates of such)
 */
public interface AnonymousExpression extends Expression {
    public Reference getReference();

    /**
     * An anonymous expression can be either created by NEW or duplicated by DUP
     *
     * in the latter case it isDuplicate
     */
    public boolean isDuplicate();
}
