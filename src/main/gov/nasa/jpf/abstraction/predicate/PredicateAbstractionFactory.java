package gov.nasa.jpf.abstraction.predicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.parser.PredicateGrammarLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicateGrammarParser;

public class PredicateAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(String[] args) {
		String file = args[1];
		String def;
		
		List<Predicate> predicates = new ArrayList<Predicate>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			while ((def = reader.readLine()) != null) {
				predicates.add(load(def));
			}
			
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read predicate file '" + file + "'");
		}

		return new PredicateAbstraction(predicates);
	}
	
	private Predicate load(String def) {	
		ANTLRInputStream chars = new ANTLRInputStream(def);
		PredicateGrammarLexer lexer = new PredicateGrammarLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PredicateGrammarParser parser = new PredicateGrammarParser(tokens);
		
		Predicate predicate = parser.predicate().val;
		
		//System.err.println(predicate.toString());

		return predicate;
	}

}
