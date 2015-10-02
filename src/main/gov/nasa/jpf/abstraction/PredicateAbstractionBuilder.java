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
package gov.nasa.jpf.abstraction;

import java.io.FileInputStream;
import java.io.IOException;

import gov.nasa.jpf.JPFConfigException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;

public class PredicateAbstractionBuilder {
    public Predicates build(String... args) {
        if (args.length > 1) {
            return build(args[1]);
        } else {
            return build();
        }
    }

    protected Predicates build(String filename) {
        try {
            ANTLRInputStream chars = new ANTLRInputStream(new FileInputStream(filename));
            PredicatesLexer lexer = new PredicatesLexer(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PredicatesParser parser = new PredicatesParser(tokens);

            return build(parser);
        } catch (IOException e) {
            System.err.println("Could not read input file '" + filename + "'");

            throw new JPFConfigException("Could not read input file '" + filename + "'");
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }

    }

    protected Predicates build(PredicatesParser parser) {
        return parser.predicates().val;
    }

    protected Predicates build() {
        return new Predicates();
    }
}
