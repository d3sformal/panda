// Generated from src/main/gov/nasa/jpf/abstraction/predicate/parser/PredicateGrammar.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.common.*;


import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

public class PredicateGrammarBaseListener implements PredicateGrammarListener {
	@Override public void enterExpression(PredicateGrammarParser.ExpressionContext ctx) { }
	@Override public void exitExpression(PredicateGrammarParser.ExpressionContext ctx) { }

	@Override public void enterTerm(PredicateGrammarParser.TermContext ctx) { }
	@Override public void exitTerm(PredicateGrammarParser.TermContext ctx) { }

	@Override public void enterPredicate(PredicateGrammarParser.PredicateContext ctx) { }
	@Override public void exitPredicate(PredicateGrammarParser.PredicateContext ctx) { }

	@Override public void enterDotpath(PredicateGrammarParser.DotpathContext ctx) { }
	@Override public void exitDotpath(PredicateGrammarParser.DotpathContext ctx) { }

	@Override public void enterFactor(PredicateGrammarParser.FactorContext ctx) { }
	@Override public void exitFactor(PredicateGrammarParser.FactorContext ctx) { }

	@Override public void enterEveryRule(ParserRuleContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }
	@Override public void visitTerminal(TerminalNode node) { }
	@Override public void visitErrorNode(ErrorNode node) { }
}