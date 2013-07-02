// Generated from src/main/gov/nasa/jpf/abstraction/predicate/grammar/Predicates.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.grammar.*;
	import gov.nasa.jpf.abstraction.predicate.grammar.impl.*;


import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

public class PredicatesBaseListener implements PredicatesListener {
	@Override public void enterExpression(PredicatesParser.ExpressionContext ctx) { }
	@Override public void exitExpression(PredicatesParser.ExpressionContext ctx) { }

	@Override public void enterPredicates(PredicatesParser.PredicatesContext ctx) { }
	@Override public void exitPredicates(PredicatesParser.PredicatesContext ctx) { }

	@Override public void enterTerm(PredicatesParser.TermContext ctx) { }
	@Override public void exitTerm(PredicatesParser.TermContext ctx) { }

	@Override public void enterPredicate(PredicatesParser.PredicateContext ctx) { }
	@Override public void exitPredicate(PredicatesParser.PredicateContext ctx) { }

	@Override public void enterPredicatelist(PredicatesParser.PredicatelistContext ctx) { }
	@Override public void exitPredicatelist(PredicatesParser.PredicatelistContext ctx) { }

	@Override public void enterContextlist(PredicatesParser.ContextlistContext ctx) { }
	@Override public void exitContextlist(PredicatesParser.ContextlistContext ctx) { }

	@Override public void enterPath(PredicatesParser.PathContext ctx) { }
	@Override public void exitPath(PredicatesParser.PathContext ctx) { }

	@Override public void enterContext(PredicatesParser.ContextContext ctx) { }
	@Override public void exitContext(PredicatesParser.ContextContext ctx) { }

	@Override public void enterContextpath(PredicatesParser.ContextpathContext ctx) { }
	@Override public void exitContextpath(PredicatesParser.ContextpathContext ctx) { }

	@Override public void enterFactor(PredicatesParser.FactorContext ctx) { }
	@Override public void exitFactor(PredicatesParser.FactorContext ctx) { }

	@Override public void enterEveryRule(ParserRuleContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }
	@Override public void visitTerminal(TerminalNode node) { }
	@Override public void visitErrorNode(ErrorNode node) { }
}