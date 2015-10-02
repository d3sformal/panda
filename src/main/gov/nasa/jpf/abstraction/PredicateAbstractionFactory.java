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
import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFConfigException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;

/**
 * A factory used to produce predicate abstraction instances from definition in an input file whose name is the first element of the @param args parameter
 */
public class PredicateAbstractionFactory extends AbstractionFactory {
    public static Predicates systemPredicates = null;

    private static String systemPredicatesFilename = "systemlibs.pred";
    private static Map<String, PredicateAbstractionBuilder> builders = new HashMap<String, PredicateAbstractionBuilder>();

    static {
        registerBuilder("predicates", new PredicateAbstractionBuilder());
        registerBuilder("signs", new SignsAbstractionBuilder());
        registerBuilder("interval", new IntervalAbstractionBuilder());
        registerBuilder("range", new RangeAbstractionBuilder());
    }

    private static void registerBuilder(String key, PredicateAbstractionBuilder builder) {
        builders.put(key, builder);
    }

    @Override
    public PredicateAbstraction create(Config config, String[]... args) {
        Predicates predicates = new Predicates();

        systemPredicates = createPredicates("predicates", systemPredicatesFilename); // Store a copy of the initial predicates
        predicates.contexts.addAll(createPredicates("predicates", systemPredicatesFilename).contexts);

        for (String[] arguments : args) {
            predicates.contexts.addAll(createPredicates(arguments).contexts);
        }

        if (config.getBoolean("panda.verbose")) {
            System.out.println(predicates.toString());
        }

        return new PredicateAbstraction(predicates);
    }

    private Predicates createPredicates(String... args) {
        String abstraction = args[0].toLowerCase();

        if (builders.containsKey(abstraction)) {
            return builders.get(abstraction).build(args);
        }

        throw new JPFConfigException("An unknown type of abstraction: '" + abstraction + "'");
    }

}
