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
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

public class FieldAssign extends Assign {

    public Field field;
    public Field newField;

    private FieldAssign(Field field, Field newField) {
        this.field = field;
        this.newField = newField;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        if (newField instanceof ObjectFieldWrite) {
            ((ObjectFieldWrite) newField).addAccessExpressionsToSet(out);
        }
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static Predicate create(Field field, Field newField) {
        return new FieldAssign(field, newField);
    }

    @Override
    public FieldAssign clone() {
        throw new PredicateNotCloneableException("Should not be copying this");
    }
}
