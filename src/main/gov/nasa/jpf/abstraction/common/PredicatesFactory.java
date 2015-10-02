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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.parser.InterpolantsLexer;
import gov.nasa.jpf.abstraction.parser.InterpolantsParser;
import gov.nasa.jpf.abstraction.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;

public class PredicatesFactory {
    private static PredicatesParser parse(String definition) {
        ANTLRInputStream chars = new ANTLRInputStream(definition);
        PredicatesLexer lexer = new PredicatesLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PredicatesParser parser = new PredicatesParser(tokens);

        return parser;
    }

    public static Predicate createPredicateFromString(String definition) {
        return parse(definition).predicate().val[0];
    }

    public static AccessExpression createAccessExpressionFromString(String definition) {
        return parse(definition).standalonepath().val[0];
    }

    public static Expression createExpressionFromString(String definition) {
        return parse(definition).standaloneexpression().val[0];
    }

    private static InterpolantsParser parseInterpolants(String definition) {
        ANTLRInputStream chars = new ANTLRInputStream(definition);
        InterpolantsLexer lexer = new InterpolantsLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        InterpolantsParser parser = new InterpolantsParser(tokens);

        return parser;
    }

    public static Predicate createInterpolantFromString(String definition) {
        return parseInterpolants(definition).standalonepredicate().val;
    }

    public static Predicate[] createInterpolantsFromString(String definition) {
        return parseInterpolants(definition).predicates().val;
    }
}
