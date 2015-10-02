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

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

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
