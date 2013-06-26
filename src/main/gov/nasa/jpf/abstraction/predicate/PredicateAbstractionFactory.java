package gov.nasa.jpf.abstraction.predicate;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.jpf.abstraction.AbstractionFactory;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.predicate.parser.PredicateGrammarLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicateGrammarParser;
import gov.nasa.jpf.abstraction.predicate.parser.PredicateGrammarParser.PredicateContext;

public class PredicateAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(String[] args) {
		String input = "not(x.y.z.a.b.c[f[x.y]] = (2 * d + (1 - 16 / c)) + aread(arr, fread(A, B)[0], x.y.z).c)";
		CharStream chars = new ANTLRInputStream(input);
		PredicateGrammarLexer lexer = new PredicateGrammarLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PredicateGrammarParser parser = new PredicateGrammarParser(tokens);

		PredicateContext predicate = parser.predicate();
		
		System.err.println(predicate.val.toString());
		
		return null;
	}
	
	public static void main(String[] args) {
		new PredicateAbstractionFactory().create(args);
	}

}
