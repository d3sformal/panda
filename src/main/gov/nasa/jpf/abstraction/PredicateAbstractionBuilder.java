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
