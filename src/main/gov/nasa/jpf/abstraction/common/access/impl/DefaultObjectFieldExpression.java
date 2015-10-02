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

import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

/**
 * Expressions accessing object fields: fread(f, o); fwrite(f, o, e);
 */
public abstract class DefaultObjectFieldExpression extends DefaultObjectAccessExpression implements ObjectFieldExpression {

    private Field field;

    protected DefaultObjectFieldExpression(AccessExpression object, Field field) {
        super(object);
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public abstract DefaultObjectFieldExpression createShallowCopy();

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
        super.addAccessSubExpressionsToSet(out);

        field.addAccessSubExpressionsToSet(out);
    }
}
