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

public class PredicateAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(String[] args) {
		String filename = args[1];
		
		try {
			ANTLRInputStream chars = new ANTLRInputStream(new FileInputStream(filename));
			PredicatesLexer lexer = new PredicatesLexer(chars);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PredicatesParser parser = new PredicatesParser(tokens);
		
			Predicates predicates = parser.predicates().val;
			
			System.out.println(predicates.toString());

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
