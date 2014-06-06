package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of stringification common to all expressions
 */
public abstract class DefaultExpression implements Expression {

    @Override
    public Expression replace(AccessExpression original, Expression replacement) {
        Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

        replacements.put(original, replacement);

        return replace(replacements);
    }

    @Override
    public final String toString() {
        return toString(Notation.policy);
    }

    @Override
    public final String toString(Notation policy) {
        return Notation.convertToString(this, policy);
    }

}
