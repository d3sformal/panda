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
package gov.nasa.jpf.abstraction.common.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Assign;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateNotCloneableException;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

public class ArraysAssign extends Assign {

    public Arrays arrays;
    public Arrays newArrays;

    private ArraysAssign(Arrays arrays, Arrays newArrays) {
        this.arrays = arrays;
        this.newArrays = newArrays;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        if (newArrays instanceof ArrayElementWrite) {
            ((ArrayElementWrite) newArrays).addAccessExpressionsToSet(out);
        }
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static Predicate create(Arrays arrays, Arrays newArrays) {
        return new ArraysAssign(arrays, newArrays);
    }

    @Override
    public ArraysAssign clone() {
        throw new PredicateNotCloneableException("Should not be copying this");
    }
}
