package gov.nasa.jpf.abstraction.predicate;

import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesParser;

import gov.nasa.jpf.Config;

/**
 * A factory used to produce predicate abstraction instances from definition in an input file whose name is the first element of the @param args parameter
 */
public class PredicateAbstractionFactory extends AbstractionFactory {

    private static String systemPredicatesFilename = "system.pred";

	@Override
	public Abstraction create(Config config, String[] args) {
		String filename = args[1];

		try {
			ANTLRInputStream chars = new ANTLRInputStream(new FileInputStream(filename));
			PredicatesLexer lexer = new PredicatesLexer(chars);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PredicatesParser parser = new PredicatesParser(tokens);

			Predicates predicates = parser.predicates().val;

			chars = new ANTLRInputStream(new FileInputStream(systemPredicatesFilename));
			lexer = new PredicatesLexer(chars);
			tokens = new CommonTokenStream(lexer);
			parser = new PredicatesParser(tokens);

			Predicates systemPredicates = parser.predicates().val;

            predicates.contexts.addAll(systemPredicates.contexts);

            if (config.getBoolean("abstract.verbose")) {
    			System.out.println(predicates.toString());
            }

			return new PredicateAbstraction(predicates);
		} catch (IOException e) {
			System.err.println("Could not read input file '" + filename + "'");
		} catch (Exception e) {
			e.printStackTrace();

			throw e;
		}
		
		return null;
	}

}
