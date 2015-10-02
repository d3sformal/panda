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

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.state.universe.Reference;

/**
 * A freshly allocated array (or a duplicate) --- not loaded from a variable.
 */
public class AnonymousArray extends AnonymousObject implements ArrayExpression {

    private Expression length;

    protected AnonymousArray(Reference reference, Expression length, boolean duplicate) {
        super(reference, duplicate);

        this.length = length;
    }

    public Expression getArrayLength() {
        return length;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AnonymousArray createShallowCopy() {
        return this;
    }

    @Override
    public boolean isSimilarToSlow(AccessExpression expression) {
        if (expression instanceof AnonymousArray) {
            AnonymousArray o = (AnonymousArray) expression;

            return getReference().equals(o.getReference());
        }

        return false;
    }

    public static AnonymousArray create(Reference reference, Expression length, boolean duplicate) {
        if (reference == null) {
            return null;
        }

        return new AnonymousArray(reference, length, duplicate);
    }

    public static AnonymousArray create(Reference reference, Expression length) {
        return create(reference, length, false);
    }

}
