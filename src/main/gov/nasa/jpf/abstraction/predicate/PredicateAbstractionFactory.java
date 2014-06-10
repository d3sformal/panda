package gov.nasa.jpf.abstraction.predicate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionContext;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * A factory used to produce predicate abstraction instances from definition in an input file whose name is the first element of the @param args parameter
 */
public class PredicateAbstractionFactory extends AbstractionFactory {

    private static class Builder {
        public Predicates build(String... args) {
            return build(args[1]);
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
    }

    private static String systemPredicatesFilename = "systemlibs.pred";
    private static Map<String, Builder> builders = new HashMap<String, Builder>();

    static {
        builders.put("predicates", new Builder());
        builders.put("signs", new Builder() {
            @Override
            public Predicates build(PredicatesParser parser) {
                Predicates predicates = new Predicates();

                for (ExpressionContext exprContext : parser.expressions().val.contexts) {
                    PredicateContext predContext = exprContext.getPredicateContext();

                    for (Expression expr : exprContext.expressions) {
                        predContext.predicates.add(LessThan.create(expr, Constant.create(0)));
                        predContext.predicates.add(LessThan.create(Constant.create(0), expr));
                    }

                    predicates.contexts.add(predContext);
                }

                return predicates;
            }
        });
        builders.put("interval", new Builder() {
            private int min;
            private int max;

            @Override
            public Predicates build(String... args) {
                min = Integer.parseInt(args[1]);
                max = Integer.parseInt(args[2]);

                return build(args[3]);
            }

            @Override
            public Predicates build(PredicatesParser parser) {
                Predicates predicates = new Predicates();

                for (ExpressionContext exprContext : parser.expressions().val.contexts) {
                    PredicateContext predContext = exprContext.getPredicateContext();

                    for (Expression expr : exprContext.expressions) {
                        predContext.predicates.add(LessThan.create(expr, Constant.create(min)));
                        predContext.predicates.add(LessThan.create(Constant.create(max), expr));
                    }

                    predicates.contexts.add(predContext);
                }

                return predicates;
            }
        });
        builders.put("range", new Builder() {
            private int min;
            private int max;

            @Override
            public Predicates build(String... args) {
                min = Integer.parseInt(args[1]);
                max = Integer.parseInt(args[2]);

                return build(args[3]);
            }

            @Override
            public Predicates build(PredicatesParser parser) {
                Predicates predicates = new Predicates();

                for (ExpressionContext exprContext : parser.expressions().val.contexts) {
                    PredicateContext predContext = exprContext.getPredicateContext();

                    for (Expression expr : exprContext.expressions) {
                        predContext.predicates.add(LessThan.create(expr, Constant.create(min)));

                        for (int i = min; i < max + 1; ++i) {
                            predContext.predicates.add(Equals.create(expr, Constant.create(i)));
                        }

                        predContext.predicates.add(LessThan.create(Constant.create(max), expr));
                    }

                    predicates.contexts.add(predContext);
                }

                return predicates;
            }
        });
    }

    @Override
    public PredicateAbstraction create(Config config, String[]... args) {
        Predicates predicates = new Predicates();

        predicates.contexts.addAll(createPredicates("predicates", systemPredicatesFilename).contexts);

        for (String[] arguments : args) {
            predicates.contexts.addAll(createPredicates(arguments).contexts);
        }

        if (config.getBoolean("apf.verbose")) {
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
