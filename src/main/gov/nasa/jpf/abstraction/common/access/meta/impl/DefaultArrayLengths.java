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
package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

/**
 * The unmodified symbol "arrlen"
 */
public class DefaultArrayLengths implements ArrayLengths {
    private static DefaultArrayLengths instance;

    public static DefaultArrayLengths create() {
        if (instance == null) {
            instance = new DefaultArrayLengths();
        }

        return instance;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultArrayLengths) {
            return true;
        }

        return false;
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
    }

    @Override
    public String toString() {
        return Notation.convertToString(this);
    }
}
