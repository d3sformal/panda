// Generated from src/main/gov/nasa/jpf/abstraction/predicate/grammar/Predicates.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.grammar.*;
	import gov.nasa.jpf.abstraction.predicate.grammar.impl.*;

import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface PredicatesListener extends ParseTreeListener {
	void enterExpression(PredicatesParser.ExpressionContext ctx);
	void exitExpression(PredicatesParser.ExpressionContext ctx);

	void enterPredicates(PredicatesParser.PredicatesContext ctx);
	void exitPredicates(PredicatesParser.PredicatesContext ctx);

	void enterTerm(PredicatesParser.TermContext ctx);
	void exitTerm(PredicatesParser.TermContext ctx);

	void enterPredicate(PredicatesParser.PredicateContext ctx);
	void exitPredicate(PredicatesParser.PredicateContext ctx);

	void enterPredicatelist(PredicatesParser.PredicatelistContext ctx);
	void exitPredicatelist(PredicatesParser.PredicatelistContext ctx);

	void enterContextlist(PredicatesParser.ContextlistContext ctx);
	void exitContextlist(PredicatesParser.ContextlistContext ctx);

	void enterPath(PredicatesParser.PathContext ctx);
	void exitPath(PredicatesParser.PathContext ctx);

	void enterContext(PredicatesParser.ContextContext ctx);
	void exitContext(PredicatesParser.ContextContext ctx);

	void enterContextpath(PredicatesParser.ContextpathContext ctx);
	void exitContextpath(PredicatesParser.ContextpathContext ctx);

	void enterFactor(PredicatesParser.FactorContext ctx);
	void exitFactor(PredicatesParser.FactorContext ctx);
}