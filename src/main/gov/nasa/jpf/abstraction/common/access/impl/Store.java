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

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

public class Store extends DefaultAccessExpression implements Root {
    private AccessExpression to;
    private Expression index;
    private Expression value;
    private Integer hashCode;

    protected Store(AccessExpression to, Expression index, Expression value) {
        super(to == null ? 1 : to.getLength());

        this.to = to;
        this.index = index;
        this.value = value;
    }

    // store(arr, a1, a2)
    public static Store create(AccessExpression a1, AccessExpression a2) {
        return new Store(null, a1, a2);
    }

    // store(a, i, e)
    public static Store create(AccessExpression a, Expression i, Expression e) {
        return new Store(a, i, e);
    }

    public boolean isRoot() {
        return to == null;
    }

    public AccessExpression getTo() {
        return to;
    }

    public Expression getIndex() {
        return index;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public Store createShallowCopy() {
        return create(to, index, value);
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Contradiction.create();
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof Store) {
            Store s = (Store) expression;

            if (isRoot()) {
                return s.isRoot();
            }

            return getTo().isSimilarToSlow(s.getTo()) ;
        }

        return false;
    }

    @Override
    public boolean isEqualToSlow(AccessExpression expression) {
        if (expression instanceof Store) {
            Store s = (Store) expression;

            if (isRoot()) {
                return s.isRoot() && getIndex().equals(s.getIndex()) && getValue().equals(s.getValue());
            }

            return getTo().equals(s.getTo()) && getIndex().equals(s.getIndex()) && getValue().equals(s.getValue());
        }

        return false;
    }

    @Override
    public Store reRoot(AccessExpression newPrefix) {
        return create(newPrefix, getIndex(), getValue());
    }

    @Override
    public AccessExpression cutTail() {
        if (isRoot()) {
            return this;
        }

        return getTo();
    }

    @Override
    public AccessExpression get(int depth) {
        if (depth > getLength()) {
            return null;
        }
        if (depth == getLength()) {
            return this;
        }
        if (isRoot()) {
            return this;
        }
        return getTo().get(depth);
    }

    @Override
    public Root getRoot() {
        if (isRoot()) {
            return this;
        }
        return getTo().getRoot();
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        AccessExpression newTo = to;
        Expression newIndex = index;
        Expression newValue = value;

        if (!isRoot()) {
            newTo = (AccessExpression) newTo.replace(replacements);
        }
        newIndex = newIndex.replace(replacements);
        newValue = newValue.replace(replacements);

        if ((isRoot() || newTo.equals(to)) && newIndex.equals(index) && newValue.equals(value)) {
            return this;
        }
        return create(newTo, newIndex, newValue);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> exprs) {
        if (!isRoot()) {
            getTo().addAccessSubExpressionsToSet(exprs);
        }
        getIndex().addAccessExpressionsToSet(exprs);
        getValue().addAccessExpressionsToSet(exprs);
    }

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = toString().hashCode();
        }

        return hashCode;
    }
}
