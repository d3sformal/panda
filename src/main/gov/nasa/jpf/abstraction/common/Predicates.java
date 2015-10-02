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
package gov.nasa.jpf.abstraction.common;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

/**
 * A container of all input predicates (read from a file) divided into individual contexts
 *
 * @see gov.nasa.jpf.abstraction.common.PredicateContext
 */
public class Predicates implements PredicatesComponentVisitable {
    public List<PredicateContext> contexts;

    public Predicates() {
        this(new LinkedList<PredicateContext>());
    }

    public Predicates(List<PredicateContext> contexts) {
        this.contexts = contexts;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return Notation.convertToString(this);
    }
}
