// Generated from src/main/gov/nasa/jpf/abstraction/predicate/parser/PredicateGrammar.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.common.*;

import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface PredicateGrammarListener extends ParseTreeListener {
	void enterExpression(PredicateGrammarParser.ExpressionContext ctx);
	void exitExpression(PredicateGrammarParser.ExpressionContext ctx);

	void enterFunpath(PredicateGrammarParser.FunpathContext ctx);
	void exitFunpath(PredicateGrammarParser.FunpathContext ctx);

	void enterTerm(PredicateGrammarParser.TermContext ctx);
	void exitTerm(PredicateGrammarParser.TermContext ctx);

	void enterPredicate(PredicateGrammarParser.PredicateContext ctx);
	void exitPredicate(PredicateGrammarParser.PredicateContext ctx);

	void enterPath(PredicateGrammarParser.PathContext ctx);
	void exitPath(PredicateGrammarParser.PathContext ctx);

	void enterDotpath(PredicateGrammarParser.DotpathContext ctx);
	void exitDotpath(PredicateGrammarParser.DotpathContext ctx);

	void enterFactor(PredicateGrammarParser.FactorContext ctx);
	void exitFactor(PredicateGrammarParser.FactorContext ctx);
}