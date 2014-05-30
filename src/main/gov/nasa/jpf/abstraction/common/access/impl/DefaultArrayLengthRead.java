package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Predicate;

/**
 * Read of an array length: alength(arrlen, a) ~ a.length
 */
public class DefaultArrayLengthRead extends DefaultArrayLengthExpression implements ArrayLengthRead {

    private Integer hashCodeValue;

    protected DefaultArrayLengthRead(AccessExpression array) {
        this(array, DefaultArrayLengths.create());
    }

    protected DefaultArrayLengthRead(AccessExpression array, ArrayLengths arrayLengths) {
        super(array, arrayLengths);
    }

    public static DefaultArrayLengthRead create(AccessExpression array) {
        if (array == null) {
            return null;
        }

        return new DefaultArrayLengthRead(array);
    }

    public static DefaultArrayLengthRead create(AccessExpression array, ArrayLengths arrayLengths) {
        if (array == null || arrayLengths == null) {
            return null;
        }

        return new DefaultArrayLengthRead(array, arrayLengths);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DefaultArrayLengthRead createShallowCopy() {
        return create(getArray(), getArrayLengths());
    }

    @Override
    public AccessExpression reRoot(AccessExpression newPrefix) {
        return create(newPrefix, getArrayLengths());
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        if (o instanceof ArrayLengthRead) {
            ArrayLengthRead r = (ArrayLengthRead) o;

            return getArrayLengths().equals(r.getArrayLengths()) && getArray().isEqualToSlow(r.getArray());
        }

        return false;
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof ArrayLengthRead) {
            ArrayLengthRead r = (ArrayLengthRead) expression;

            return getArray().isSimilarToSlow(r.getArray());
        }

        return false;
    }

    @Override
    public boolean isSimilarToPrefixOf(AccessExpression path) {
        if (path instanceof ArrayLengthRead) {
            ArrayLengthRead r = (ArrayLengthRead) path;

            return getObject().isSimilarToPrefixOf(r.getObject()) && getArray().isSimilarToPrefixOf(r.getArray());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (hashCodeValue == null) {
            hashCodeValue = ("read_length_" + getObject().hashCode()).hashCode();
        }

        return hashCodeValue;
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        AccessExpression newA = getObject().replaceSubExpressions(replacements);

        if (newA == getObject()) return this;
        else return create(newA, getArrayLengths());
    }

    /**
     * @see gov.nasa.jpf.abstraction.common.Predicate.update for an overall view
     */
    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression updated = getObject().update(expression, newExpression);

        // there is no direct assignment to length, therefore the update must have happend in the prefix (if it affects this expression at all)

        // the prefix is a new array
        // e.g.
        //   original expression: alength(arrlen, x)
        //   statement: x := new Integer[10]
        if (updated instanceof AnonymousArray) {
            AnonymousArray updatedAnonymousArray = (AnonymousArray) updated;

            return updatedAnonymousArray.getArrayLength();
            //return create(getArray(), DefaultArrayLengthWrite.create(expression, getArrayLengths(), updatedAnonymousArray.getArrayLength()));
        }

        // the prefix is an access expression, as is the case with `alength(arrlen, y.z)` where the access expression is `y.z`
        if (updated instanceof AccessExpression) {
            AccessExpression updatedAccessExpression = (AccessExpression) updated;

            if (newExpression instanceof AnonymousArray) {
                AnonymousArray aa = (AnonymousArray) newExpression;

                // x := new Integer[10]
                // Scenario1 alength(arrlen, y.z): alength(alengthwrite(arrlen, fresh, 10), y.z)
                // Scenario2 alength(arrlen, x):   alength(alengthwrite(arrlen, fresh, 10), fresh)
                return create(updatedAccessExpression, DefaultArrayLengthWrite.create(aa, getArrayLengths(), aa.getArrayLength()));
            }

            // x := a
            // alength(arrlen, update(y.z, x, a))
            return create(updatedAccessExpression, getArrayLengths());
        }

        /**
         * Updated object cannot be an array, something went wrong, propagate
         */
        return UndefinedAccessExpression.create();
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        if (getArrayLengths() instanceof ArrayLengthWrite) {
            ArrayLengthWrite w = (ArrayLengthWrite) getArrayLengths();

            return Conjunction.create(Equals.create(getArray(), w.getArray()), w.getPreconditionForBeingFresh());
        }

        return Contradiction.create();
    }
}
