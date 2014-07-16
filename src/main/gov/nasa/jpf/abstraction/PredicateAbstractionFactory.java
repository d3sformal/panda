package gov.nasa.jpf.abstraction;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.parser.PredicatesParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * A factory used to produce predicate abstraction instances from definition in an input file whose name is the first element of the @param args parameter
 */
public class PredicateAbstractionFactory extends AbstractionFactory {

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
