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

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;

/**
 * Expressions fwrite(f, o, e) ~ o.f := e
 */
public class DefaultObjectFieldWrite extends DefaultObjectFieldExpression implements ObjectFieldWrite {

    private Expression newValue;
    private Integer hashCodeValue;

    protected DefaultObjectFieldWrite(AccessExpression object, String name, Expression newValue) {
        this(object, DefaultField.create(name), newValue);
    }

    protected DefaultObjectFieldWrite(AccessExpression object, Field field, Expression newValue) {
        super(object, field);

        this.newValue = newValue;
    }

    @Override
    public Expression getNewValue() {
        return newValue;
    }

    public static DefaultObjectFieldWrite create(AccessExpression object, String name, Expression newValue) {
        if (object == null || name == null || newValue == null) {
            return null;
        }

        return new DefaultObjectFieldWrite(object, name, newValue);
    }

    public static DefaultObjectFieldWrite create(AccessExpression object, Field field, Expression newValue) {
        if (object == null || field == null || newValue == null) {
            return null;
        }

        return new DefaultObjectFieldWrite(object, field, newValue);
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
        super.addAccessSubExpressionsToSet(out);

        newValue.addAccessExpressionsToSet(out);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DefaultObjectFieldWrite createShallowCopy() {
        return create(getObject(), getName(), getNewValue());
    }

    @Override
    public String getName() {
        return getField().getName();
    }

    @Override
    public AccessExpression reRoot(AccessExpression newPrefix) {
        return create(newPrefix, getField(), getNewValue());
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        if (o instanceof ObjectFieldWrite) {
            ObjectFieldWrite w = (ObjectFieldWrite) o;

            return getObject().isEqualToSlow(w.getObject()) && getField().equals(w.getField()) && getNewValue().equals(w.getNewValue());
        }

        return false;
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof ObjectFieldWrite) {
            ObjectFieldWrite w = (ObjectFieldWrite) expression;

            return getObject().isSimilarToSlow(w.getObject());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (hashCodeValue == null) {
            hashCodeValue = ("write_field_" + getObject().hashCode() + "_" + getField().getName().hashCode() + "_" + getNewValue().hashCode()).hashCode();
        }

        return hashCodeValue;
    }

    @Override
    public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
        AccessExpression newO = getObject().replaceSubExpressions(replacements);
        Expression newNV = getNewValue().replace(replacements);

        if (newO == getObject() && newNV == getNewValue()) return this;
        else return create(newO, getField(), newNV);
    }

    @Override
    public Predicate getPreconditionForBeingFresh() {
        return getNewValue().getPreconditionForBeingFresh();
    }

}
