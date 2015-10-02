/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.impl.DefaultObjectExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.smt.PredicatesSMTStringifier;

/**
 * Implements common behaviour of most of the access expression elements
 */
public abstract class DefaultAccessExpression extends DefaultObjectExpression implements AccessExpression {

    protected int length;

    protected DefaultAccessExpression(int length) {
        this.length = length;
    }

    /**
     * Retrieves all access expressions present in this expression
     *
     * that is:
     * 1) this expression itself
     * 2) any access expression present in an array index
     * 3) any access expression present in an update expression (fwrite, awrite, alengthupdate ...)
     */
    @Override
    public final void addAccessExpressionsToSet(Set<AccessExpression> out) {
        addAccessSubExpressionsToSet(out);
        out.add(this);
    }

    @Override
    public final void addAllPrefixesToSet(Set<AccessExpression> out) {
        AccessExpression prefix = this;

        while (!(prefix instanceof Root)) {
            out.add(prefix);
            prefix = prefix.cutTail();
        }

        out.add(prefix);
    }

    @Override
    public final boolean isEqualTo(AccessExpression expression) {
        // First check lengths ... quick
        // Secondly check roots ... quickly exclude expressions rooted in different local variables / classes (fast for roots)
        // Lastly perform recursive check ... slow

        return getLength() == expression.getLength() && getRoot().isEqualToSlow(expression.getRoot()) && isEqualToSlow(expression);
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof AccessExpression) {
            AccessExpression ae = (AccessExpression) o;

            if (hashCode() != o.hashCode()) {
                return false;
            }

            return isEqualTo(ae);
        }

        return false;
    }

    @Override
    public final boolean isSimilarTo(AccessExpression expression) {
        // First check lengths ... quick
        // Secondly check roots ... quickly exclude expressions rooted in different local variables / classes (fast for roots)
        // Lastly perform recursive check ... slow

        return getLength() == expression.getLength() && getRoot().isSimilarToSlow(expression.getRoot()) && isSimilarToSlow(expression);
    }

    /**
     * Replaces occurances of access expressions by other expressions
     */
    @Override
    public final Expression replace(Map<AccessExpression, Expression> replacements) {
        for (AccessExpression expression : replacements.keySet()) {
            Expression newExpression = replacements.get(expression);

            if (equals(expression)) {
                return newExpression;
            }
        }

        AccessExpression path = this;

        for (AccessExpression expression : replacements.keySet()) {
            Expression newExpression = replacements.get(expression);

            if (expression.isPrefixOf(path) && newExpression instanceof AccessExpression) {
                AccessExpression newPrefix = (AccessExpression) newExpression;

                path = path.reRoot(expression, newPrefix);

                break; // Do not chain the replacements
            }
        }

        return path.replaceSubExpressions(replacements);
    }

    /**
     * Transforms the access expression in such a way that it reflects all updates (fwrite, awrite...) in affected subexpressions (fread, aread).
     *
     * update(fread(f, o), o.f, 3) = fread(fwrite(f, o, 3), o)
     * update(fread(f, o), p.f, 3) = fread(fwrite(f, p, 3), o)
     * ...
     */
    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        return this;
    }

    @Override
    public abstract DefaultAccessExpression createShallowCopy();

    /**
     * Is this expression referring (entirely) to the java keyword this
     */
    @Override
    public final boolean isThis() {
        if (this instanceof Root) {
            Root r = (Root) this;

            return r.getName().equals("this");
        }

        return false;
    }

    /**
     * Is the expression accessing a variable through a static context
     */
    @Override
    public final boolean isStatic() {
        return getRoot() instanceof PackageAndClass;
    }

    /**
     * Is the expression accessing a local variable
     */
    @Override
    public final boolean isLocalVariable() {
        return this instanceof Root && !isStatic() && !isReturnValue() && !(this instanceof AnonymousExpression);
    }

    /**
     * Is the expression referring to the keyword return
     */
    @Override
    public final boolean isReturnValue() {
        return this instanceof ReturnValue;
    }

    /**
     * Is this expression a prefix of the supplied expression
     *
     * a.b.c is a prefix of a.b.c, a.b.c.d ...
     * a.b.c is not a prefix of a.b, a.b.d ...
     */
    @Override
    public boolean isPrefixOf(AccessExpression path) {
        if (getLength() > path.getLength()) {
            return false;
        }

        return equals(path.get(getLength()));
    }

    /**
     * Is the expression a prefix (up to different array indices) of the supplied expression
     *
     * a[i] is similar to a prefix of a[k], a[k].b, a[i][d]
     * a[i] is not similar to a prefix of b[i], a.b
     */
    @Override
    public boolean isSimilarToPrefixOf(AccessExpression path) {
        if (getLength() > path.getLength()) {
            return false;
        }

        return isSimilarTo(path.get(getLength()));
    }

    /**
     * Is the expression a prefix of the supplied expression and is its length strictly lower
     *
     * a.b is a proper prefix of a.b.c but not a proper prefix of a.b
     */
    @Override
    public final boolean isProperPrefixOf(AccessExpression expression) {
        return getLength() < expression.getLength() && isPrefixOf(expression);
    }

    /**
     * Changes the prefix of this expression from oldPrefix to newPrefix
     *
     * reRoot(a.b.c.d, a.b, x.y) = x.y.c.d
     */
    @Override
    public final AccessExpression reRoot(AccessExpression oldPrefix, AccessExpression newPrefix) {
        if (oldPrefix.isPrefixOf(this)) {
            // get the last element of the prefix ("b" in "o.p.a.b")
            AccessExpression oldPrefixEnd = get(oldPrefix.getLength());

            // fallback value in the case of "this.equals(oldPrefix)"
            // used when the loop body not executed at all
            AccessExpression result = newPrefix;

            // idea: we must create a new shallow copy of the suffix (each element) because the reference to its parent object is changed here
            // using the copy-on-write mechanism

            AccessExpression currentExpr = this;
            DefaultObjectAccessExpression currentExprCopy = null;
            DefaultObjectAccessExpression previousExprCopy = null;

            while (currentExpr != oldPrefixEnd && currentExpr instanceof DefaultObjectAccessExpression) {
                currentExprCopy = (DefaultObjectAccessExpression) currentExpr.createShallowCopy();

                // update cached root, as the prefix may now have a different root
                currentExprCopy.root = newPrefix.getRoot();

                // update cached length, as the prefix may now have a different length
                currentExprCopy.length += newPrefix.getLength() - oldPrefix.getLength();

                // we must overwrite the fallback now (in the first iteration of the loop)
                // situation: oldPrefix is not the whole expression ("this")
                if (result == newPrefix) {
                    result = currentExprCopy;
                }

                // change parent of the shallow copy from previous iteration (next element of the suffix)
                if (previousExprCopy != null) {
                    previousExprCopy.setObject(currentExprCopy);
                }

                previousExprCopy = currentExprCopy;

                // move to the preceding element of the suffix (towards the "oldPrefixEnd")
                currentExpr = ((DefaultObjectAccessExpression) currentExpr).getObject();
            }

            // insert the new prefix
            // this happens only when the loop was executed at least once
            if (previousExprCopy != null) {
                previousExprCopy.setObject(newPrefix);
            }

            return result;
        }

        return null;
    }

    @Override
    public final int getLength() {
        return length;
    }

}
