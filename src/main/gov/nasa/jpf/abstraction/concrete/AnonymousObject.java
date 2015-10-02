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
package gov.nasa.jpf.abstraction.concrete;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.state.universe.Reference;

/**
 * Represents a freshly allocated object (freshly allocated = not loaded from a variable)
 *
 * A copy of the object reference (created by DUP instruction) may have been stored.
 */
public class AnonymousObject extends DefaultAccessExpression implements Root, AnonymousExpression {

    private Reference reference;
    private String name;

    /**
     * Flag determining whether the anonymous object represents a value directly pushed to the stack by NEW (`false`) or DUP (`true`)
     */
    private boolean duplicate;

    protected AnonymousObject(Reference reference, boolean duplicate) {
        super(1);

        this.reference = reference;
        this.duplicate = duplicate;
    }

    @Override
    public Reference getReference() {
        return reference;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AnonymousObject createShallowCopy() {
        return this;
    }

    public static AnonymousObject create(Reference reference, boolean duplicate) {
        if (reference == null) {
            return null;
        }

        return new AnonymousObject(reference, duplicate);
    }

    public static AnonymousObject create(Reference reference) {
        return create(reference, false);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        return this;
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return Tautology.create();
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        return this;
    }

    @Override
    public Root getRoot() {
        return this;
    }

    @Override
    public AccessExpression get(int depth) {
        if (depth == 1) {
            return this;
        }

        return null;
    }

    @Override
    public AccessExpression cutTail() {
        return this;
    }

    @Override
    public AccessExpression reRoot(AccessExpression newPrefix) {
        return newPrefix;
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof AnonymousObject) {
            AnonymousObject o = (AnonymousObject) expression;

            return getReference().equals(o.getReference());
        }

        return false;
    }

    @Override
    public String getName() {
        if (name == null) {
            name = "ref(" + reference + ")";
        }

        return name;
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        if (o instanceof AnonymousObject) {
            AnonymousObject ao = (AnonymousObject) o;

            return getReference().equals(ao.getReference());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getReference().getReferenceNumber();
    }

    @Override
    public boolean isDuplicate() {
        return duplicate;
    }

}
