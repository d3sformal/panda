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

import gov.nasa.jpf.abstraction.common.impl.PredicatesDotStringifier;
import gov.nasa.jpf.abstraction.common.impl.PredicatesFunctionStringifier;
import gov.nasa.jpf.abstraction.smt.PredicatesSMTStringifier;

/**
 * Notation types for printing predicates.
 *
 * dot-notation:
 *
 * a.b ... field accesses
 * a[0] ... array element accesses
 *
 * function notation:
 *
 * fread(b, a) ... field accesses
 * aread(arr, a, 0) ... array element accesses
 */
public enum Notation {
    DOT_NOTATION,
    FUNCTION_NOTATION,
    SMT_NOTATION;

    public static Notation policy = Notation.FUNCTION_NOTATION;

    public static PredicatesStringifier getStringifier(Notation policy) {
        switch (policy) {
        case DOT_NOTATION:
            return new PredicatesDotStringifier();
        case FUNCTION_NOTATION:
            return new PredicatesFunctionStringifier();
        case SMT_NOTATION:
            return new PredicatesSMTStringifier();
        }

        return null;
    }

    public static PredicatesStringifier getDefaultStringifier() {
        return getStringifier(policy);
    }

    public static String convertToString(PredicatesComponentVisitable visitable) {
        return convertToString(visitable, policy);
    }

    public static String convertToString(PredicatesComponentVisitable visitable, Notation policy) {
        return convertToString(visitable, getStringifier(policy));
    }

    public static String convertToString(PredicatesComponentVisitable visitable, PredicatesStringifier stringifier) {
        visitable.accept(stringifier);

        return stringifier.getString();
    }
}
