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

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

/**
 * Read/Write to an array element aread(arr, a, i); awrite(arr, a, i, e);
 */
public abstract class DefaultArrayElementExpression extends DefaultArrayAccessExpression implements ArrayElementExpression {

    private Expression index;
    private Arrays arrays;

    protected DefaultArrayElementExpression(AccessExpression array, Arrays arrays, Expression index) {
        super(array);

        this.arrays = arrays;
        this.index = index;
    }

    @Override
    public Expression getIndex() {
        return index;
    }

    @Override
    public Arrays getArrays() {
        return arrays;
    }

    @Override
    public abstract DefaultArrayElementExpression createShallowCopy();

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
        super.addAccessSubExpressionsToSet(out);

        arrays.addAccessSubExpressionsToSet(out);
        index.addAccessExpressionsToSet(out);
    }
}
