// Generated from src/main/gov/nasa/jpf/abstraction/predicate/grammar/Predicates.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.grammar.*;
	import gov.nasa.jpf.abstraction.predicate.grammar.impl.*;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PredicatesParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__20=1, T__19=2, T__18=3, T__17=4, T__16=5, T__15=6, T__14=7, T__13=8, 
		T__12=9, T__11=10, T__10=11, T__9=12, T__8=13, T__7=14, T__6=15, T__5=16, 
		T__4=17, T__3=18, T__2=19, T__1=20, T__0=21, CONSTANT=22, ID=23, WS=24;
	public static final String[] tokenNames = {
		"<INVALID>", "']'", "')'", "'.'", "','", "'+'", "'['", "'-'", "'*'", "'('", 
		"'not'", "'<'", "'='", "'false'", "'object'", "'method'", "'/'", "'aread'", 
		"'true'", "'static'", "'fread'", "'arr'", "CONSTANT", "ID", "WS"
	};
	public static final int
		RULE_predicates = 0, RULE_contextlist = 1, RULE_context = 2, RULE_predicatelist = 3, 
		RULE_predicate = 4, RULE_expression = 5, RULE_term = 6, RULE_factor = 7, 
		RULE_contextpath = 8, RULE_path = 9;
	public static final String[] ruleNames = {
		"predicates", "contextlist", "context", "predicatelist", "predicate", 
		"expression", "term", "factor", "contextpath", "path"
	};

	@Override
	public String getGrammarFileName() { return "Predicates.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public PredicatesParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class PredicatesContext extends ParserRuleContext {
		public Predicates val;
		public ContextlistContext cs;
		public ContextlistContext contextlist() {
			return getRuleContext(ContextlistContext.class,0);
		}
		public PredicatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterPredicates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitPredicates(this);
		}
	}

	public final PredicatesContext predicates() throws RecognitionException {
		PredicatesContext _localctx = new PredicatesContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_predicates);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20); ((PredicatesContext)_localctx).cs = contextlist(0);

					((PredicatesContext)_localctx).val =  new Predicates(((PredicatesContext)_localctx).cs.val);
				
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContextlistContext extends ParserRuleContext {
		public int _p;
		public List<Context> val;
		public ContextlistContext cs;
		public ContextContext c;
		public ContextlistContext contextlist() {
			return getRuleContext(ContextlistContext.class,0);
		}
		public ContextContext context() {
			return getRuleContext(ContextContext.class,0);
		}
		public ContextlistContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ContextlistContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_contextlist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterContextlist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitContextlist(this);
		}
	}

	public final ContextlistContext contextlist(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ContextlistContext _localctx = new ContextlistContext(_ctx, _parentState, _p);
		ContextlistContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, RULE_contextlist);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{

					((ContextlistContext)_localctx).val =  new ArrayList<Context>();
				
			}
			_ctx.stop = _input.LT(-1);
			setState(32);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ContextlistContext(_parentctx, _parentState, _p);
					_localctx.cs = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_contextlist);
					setState(26);
					if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
					setState(27); ((ContextlistContext)_localctx).c = context();

					          		((ContextlistContext)_localctx).val =  ((ContextlistContext)_localctx).cs.val;
					          		_localctx.val.add(((ContextlistContext)_localctx).c.val);
					          	
					}
					} 
				}
				setState(34);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ContextContext extends ParserRuleContext {
		public Context val;
		public PredicatelistContext ps;
		public ContextpathContext c;
		public PredicatelistContext predicatelist() {
			return getRuleContext(PredicatelistContext.class,0);
		}
		public ContextpathContext contextpath() {
			return getRuleContext(ContextpathContext.class,0);
		}
		public ContextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_context; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterContext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitContext(this);
		}
	}

	public final ContextContext context() throws RecognitionException {
		ContextContext _localctx = new ContextContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_context);
		try {
			setState(55);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(35); match(6);
				setState(36); match(19);
				setState(37); match(1);
				setState(38); ((ContextContext)_localctx).ps = predicatelist(0);

						((ContextContext)_localctx).val =  new StaticContext(((ContextContext)_localctx).ps.val);
					
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(41); match(6);
				setState(42); match(14);
				setState(43); ((ContextContext)_localctx).c = contextpath(0);
				setState(44); match(1);
				setState(45); ((ContextContext)_localctx).ps = predicatelist(0);

						((ContextContext)_localctx).val =  new ObjectContext(((ContextContext)_localctx).c.val, ((ContextContext)_localctx).ps.val);
					
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(48); match(6);
				setState(49); match(15);
				setState(50); ((ContextContext)_localctx).c = contextpath(0);
				setState(51); match(1);
				setState(52); ((ContextContext)_localctx).ps = predicatelist(0);

						((ContextContext)_localctx).val =  new MethodContext(((ContextContext)_localctx).c.val, ((ContextContext)_localctx).ps.val);
					
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicatelistContext extends ParserRuleContext {
		public int _p;
		public List<Predicate> val;
		public PredicatelistContext ps;
		public PredicateContext p;
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public PredicatelistContext predicatelist() {
			return getRuleContext(PredicatelistContext.class,0);
		}
		public PredicatelistContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public PredicatelistContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_predicatelist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterPredicatelist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitPredicatelist(this);
		}
	}

	public final PredicatelistContext predicatelist(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PredicatelistContext _localctx = new PredicatelistContext(_ctx, _parentState, _p);
		PredicatelistContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, RULE_predicatelist);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{

					((PredicatelistContext)_localctx).val =  new ArrayList<Predicate>();
				
			}
			_ctx.stop = _input.LT(-1);
			setState(66);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PredicatelistContext(_parentctx, _parentState, _p);
					_localctx.ps = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_predicatelist);
					setState(60);
					if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
					setState(61); ((PredicatelistContext)_localctx).p = predicate();

					          		((PredicatelistContext)_localctx).val =  ((PredicatelistContext)_localctx).ps.val;
					          		_localctx.val.add(((PredicatelistContext)_localctx).p.val);
					          	
					}
					} 
				}
				setState(68);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public Predicate val;
		public PredicateContext p;
		public ExpressionContext a;
		public ExpressionContext b;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitPredicate(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_predicate);
		try {
			setState(89);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(69); match(18);

						((PredicateContext)_localctx).val =  new Equals(new Constant(1), new Constant(1));
					
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(71); match(13);

						((PredicateContext)_localctx).val =  new Equals(new Constant(0), new Constant(1));
					
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(73); match(10);
				setState(74); match(9);
				setState(75); ((PredicateContext)_localctx).p = predicate();
				setState(76); match(2);

						((PredicateContext)_localctx).val =  new Negation(((PredicateContext)_localctx).p.val);
					
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(79); ((PredicateContext)_localctx).a = expression();
				setState(80); match(12);
				setState(81); ((PredicateContext)_localctx).b = expression();

						((PredicateContext)_localctx).val =  new Equals(((PredicateContext)_localctx).a.val, ((PredicateContext)_localctx).b.val);
					
				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(84); ((PredicateContext)_localctx).a = expression();
				setState(85); match(11);
				setState(86); ((PredicateContext)_localctx).b = expression();

						((PredicateContext)_localctx).val =  new LessThan(((PredicateContext)_localctx).a.val, ((PredicateContext)_localctx).b.val);
					
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public Expression val;
		public TermContext t;
		public TermContext a;
		public TermContext b;
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expression);
		try {
			setState(104);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(91); ((ExpressionContext)_localctx).t = term();

						((ExpressionContext)_localctx).val =  ((ExpressionContext)_localctx).t.val;
					
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(94); ((ExpressionContext)_localctx).a = term();
				setState(95); match(5);
				setState(96); ((ExpressionContext)_localctx).b = term();

						((ExpressionContext)_localctx).val =  new Add(((ExpressionContext)_localctx).a.val, ((ExpressionContext)_localctx).b.val);
					
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(99); ((ExpressionContext)_localctx).a = term();
				setState(100); match(7);
				setState(101); ((ExpressionContext)_localctx).b = term();

						((ExpressionContext)_localctx).val =  new Subtract(((ExpressionContext)_localctx).a.val, ((ExpressionContext)_localctx).b.val);
					
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public Expression val;
		public FactorContext f;
		public FactorContext a;
		public FactorContext b;
		public List<FactorContext> factor() {
			return getRuleContexts(FactorContext.class);
		}
		public FactorContext factor(int i) {
			return getRuleContext(FactorContext.class,i);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_term);
		try {
			setState(119);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(106); ((TermContext)_localctx).f = factor();

						((TermContext)_localctx).val =  ((TermContext)_localctx).f.val;
					
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(109); ((TermContext)_localctx).a = factor();
				setState(110); match(8);
				setState(111); ((TermContext)_localctx).b = factor();

						((TermContext)_localctx).val =  new Multiply(_localctx.a.val, _localctx.b.val);
					
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(114); ((TermContext)_localctx).a = factor();
				setState(115); match(16);
				setState(116); ((TermContext)_localctx).b = factor();

						((TermContext)_localctx).val =  new Divide(_localctx.a.val, _localctx.b.val);
					
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FactorContext extends ParserRuleContext {
		public Expression val;
		public Token CONSTANT;
		public PathContext p;
		public ExpressionContext e;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode CONSTANT() { return getToken(PredicatesParser.CONSTANT, 0); }
		public PathContext path() {
			return getRuleContext(PathContext.class,0);
		}
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterFactor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitFactor(this);
		}
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_factor);
		try {
			setState(131);
			switch (_input.LA(1)) {
			case CONSTANT:
				enterOuterAlt(_localctx, 1);
				{
				setState(121); ((FactorContext)_localctx).CONSTANT = match(CONSTANT);

						((FactorContext)_localctx).val =  new Constant(Integer.parseInt((((FactorContext)_localctx).CONSTANT!=null?((FactorContext)_localctx).CONSTANT.getText():null)));
					
				}
				break;
			case 17:
			case 20:
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(123); ((FactorContext)_localctx).p = path(0);

						((FactorContext)_localctx).val =  ((FactorContext)_localctx).p.val;
					
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 3);
				{
				setState(126); match(9);
				setState(127); ((FactorContext)_localctx).e = expression();
				setState(128); match(2);

						((FactorContext)_localctx).val =  ((FactorContext)_localctx).e.val;
					
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContextpathContext extends ParserRuleContext {
		public int _p;
		public AccessPath val;
		public ContextpathContext p;
		public Token f;
		public TerminalNode ID() { return getToken(PredicatesParser.ID, 0); }
		public ContextpathContext contextpath() {
			return getRuleContext(ContextpathContext.class,0);
		}
		public ContextpathContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ContextpathContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_contextpath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterContextpath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitContextpath(this);
		}
	}

	public final ContextpathContext contextpath(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ContextpathContext _localctx = new ContextpathContext(_ctx, _parentState, _p);
		ContextpathContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, RULE_contextpath);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(134); ((ContextpathContext)_localctx).f = match(ID);

					((ContextpathContext)_localctx).val =  new AccessPath((((ContextpathContext)_localctx).f!=null?((ContextpathContext)_localctx).f.getText():null));
				
			}
			_ctx.stop = _input.LT(-1);
			setState(143);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ContextpathContext(_parentctx, _parentState, _p);
					_localctx.p = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_contextpath);
					setState(137);
					if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
					setState(138); match(3);
					setState(139); ((ContextpathContext)_localctx).f = match(ID);

					          		((ContextpathContext)_localctx).val =  ((ContextpathContext)_localctx).p.val;
					          		_localctx.val.appendSubElement((((ContextpathContext)_localctx).f!=null?((ContextpathContext)_localctx).f.getText():null));
					          	
					}
					} 
				}
				setState(145);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PathContext extends ParserRuleContext {
		public int _p;
		public AccessPath val;
		public PathContext p;
		public Token f;
		public ExpressionContext e;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PathContext path() {
			return getRuleContext(PathContext.class,0);
		}
		public TerminalNode ID() { return getToken(PredicatesParser.ID, 0); }
		public PathContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public PathContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_path; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).enterPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicatesListener ) ((PredicatesListener)listener).exitPath(this);
		}
	}

	public final PathContext path(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PathContext _localctx = new PathContext(_ctx, _parentState, _p);
		PathContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, RULE_path);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			switch (_input.LA(1)) {
			case ID:
				{
				setState(147); ((PathContext)_localctx).f = match(ID);

						((PathContext)_localctx).val =  new AccessPath((((PathContext)_localctx).f!=null?((PathContext)_localctx).f.getText():null));
					
				}
				break;
			case 20:
				{
				setState(149); match(20);
				setState(150); match(9);
				setState(151); ((PathContext)_localctx).f = match(ID);
				setState(152); match(4);
				setState(153); ((PathContext)_localctx).p = path(0);
				setState(154); match(2);

						((PathContext)_localctx).val =  ((PathContext)_localctx).p.val;
						_localctx.val.appendSubElement((((PathContext)_localctx).f!=null?((PathContext)_localctx).f.getText():null));
					
				}
				break;
			case 17:
				{
				setState(157); match(17);
				setState(158); match(9);
				setState(159); match(21);
				setState(160); match(4);
				setState(161); ((PathContext)_localctx).p = path(0);
				setState(162); match(4);
				setState(163); ((PathContext)_localctx).e = expression();
				setState(164); match(2);

						((PathContext)_localctx).val =  ((PathContext)_localctx).p.val;
						_localctx.val.appendIndexElement(((PathContext)_localctx).e.val);
					
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(181);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(179);
					switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
					case 1:
						{
						_localctx = new PathContext(_parentctx, _parentState, _p);
						_localctx.p = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_path);
						setState(169);
						if (!(4 >= _localctx._p)) throw new FailedPredicateException(this, "4 >= $_p");
						setState(170); match(3);
						setState(171); ((PathContext)_localctx).f = match(ID);

						          		((PathContext)_localctx).val =  ((PathContext)_localctx).p.val;
						          		_localctx.val.appendSubElement((((PathContext)_localctx).f!=null?((PathContext)_localctx).f.getText():null));
						          	
						}
						break;

					case 2:
						{
						_localctx = new PathContext(_parentctx, _parentState, _p);
						_localctx.p = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_path);
						setState(173);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						setState(174); match(6);
						setState(175); ((PathContext)_localctx).e = expression();
						setState(176); match(1);

						          		((PathContext)_localctx).val =  ((PathContext)_localctx).p.val;
						          		_localctx.val.appendIndexElement(((PathContext)_localctx).e.val);
						          	
						}
						break;
					}
					} 
				}
				setState(183);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1: return contextlist_sempred((ContextlistContext)_localctx, predIndex);

		case 3: return predicatelist_sempred((PredicatelistContext)_localctx, predIndex);

		case 8: return contextpath_sempred((ContextpathContext)_localctx, predIndex);

		case 9: return path_sempred((PathContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean predicatelist_sempred(PredicatelistContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1: return 1 >= _localctx._p;
		}
		return true;
	}
	private boolean contextlist_sempred(ContextlistContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 1 >= _localctx._p;
		}
		return true;
	}
	private boolean path_sempred(PathContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3: return 4 >= _localctx._p;

		case 4: return 3 >= _localctx._p;
		}
		return true;
	}
	private boolean contextpath_sempred(ContextpathContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return 1 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\2\3\32\u00bb\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b"+
		"\4\t\t\t\4\n\t\n\4\13\t\13\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3"+
		"!\n\3\f\3\16\3$\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4:\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7"+
		"\5C\n\5\f\5\16\5F\13\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\\\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\5\7k\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\5\bz\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t"+
		"\u0086\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0090\n\n\f\n\16\n\u0093"+
		"\13\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u00aa\n\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u00b6\n\13\f\13\16\13\u00b9\13"+
		"\13\3\13\2\f\2\4\6\b\n\f\16\20\22\24\2\2\u00c3\2\26\3\2\2\2\4\31\3\2\2"+
		"\2\69\3\2\2\2\b;\3\2\2\2\n[\3\2\2\2\fj\3\2\2\2\16y\3\2\2\2\20\u0085\3"+
		"\2\2\2\22\u0087\3\2\2\2\24\u00a9\3\2\2\2\26\27\5\4\3\2\27\30\b\2\1\2\30"+
		"\3\3\2\2\2\31\32\b\3\1\2\32\33\b\3\1\2\33\"\3\2\2\2\34\35\6\3\2\3\35\36"+
		"\5\6\4\2\36\37\b\3\1\2\37!\3\2\2\2 \34\3\2\2\2!$\3\2\2\2\" \3\2\2\2\""+
		"#\3\2\2\2#\5\3\2\2\2$\"\3\2\2\2%&\7\b\2\2&\'\7\25\2\2\'(\7\3\2\2()\5\b"+
		"\5\2)*\b\4\1\2*:\3\2\2\2+,\7\b\2\2,-\7\20\2\2-.\5\22\n\2./\7\3\2\2/\60"+
		"\5\b\5\2\60\61\b\4\1\2\61:\3\2\2\2\62\63\7\b\2\2\63\64\7\21\2\2\64\65"+
		"\5\22\n\2\65\66\7\3\2\2\66\67\5\b\5\2\678\b\4\1\28:\3\2\2\29%\3\2\2\2"+
		"9+\3\2\2\29\62\3\2\2\2:\7\3\2\2\2;<\b\5\1\2<=\b\5\1\2=D\3\2\2\2>?\6\5"+
		"\3\3?@\5\n\6\2@A\b\5\1\2AC\3\2\2\2B>\3\2\2\2CF\3\2\2\2DB\3\2\2\2DE\3\2"+
		"\2\2E\t\3\2\2\2FD\3\2\2\2GH\7\24\2\2H\\\b\6\1\2IJ\7\17\2\2J\\\b\6\1\2"+
		"KL\7\f\2\2LM\7\13\2\2MN\5\n\6\2NO\7\4\2\2OP\b\6\1\2P\\\3\2\2\2QR\5\f\7"+
		"\2RS\7\16\2\2ST\5\f\7\2TU\b\6\1\2U\\\3\2\2\2VW\5\f\7\2WX\7\r\2\2XY\5\f"+
		"\7\2YZ\b\6\1\2Z\\\3\2\2\2[G\3\2\2\2[I\3\2\2\2[K\3\2\2\2[Q\3\2\2\2[V\3"+
		"\2\2\2\\\13\3\2\2\2]^\5\16\b\2^_\b\7\1\2_k\3\2\2\2`a\5\16\b\2ab\7\7\2"+
		"\2bc\5\16\b\2cd\b\7\1\2dk\3\2\2\2ef\5\16\b\2fg\7\t\2\2gh\5\16\b\2hi\b"+
		"\7\1\2ik\3\2\2\2j]\3\2\2\2j`\3\2\2\2je\3\2\2\2k\r\3\2\2\2lm\5\20\t\2m"+
		"n\b\b\1\2nz\3\2\2\2op\5\20\t\2pq\7\n\2\2qr\5\20\t\2rs\b\b\1\2sz\3\2\2"+
		"\2tu\5\20\t\2uv\7\22\2\2vw\5\20\t\2wx\b\b\1\2xz\3\2\2\2yl\3\2\2\2yo\3"+
		"\2\2\2yt\3\2\2\2z\17\3\2\2\2{|\7\30\2\2|\u0086\b\t\1\2}~\5\24\13\2~\177"+
		"\b\t\1\2\177\u0086\3\2\2\2\u0080\u0081\7\13\2\2\u0081\u0082\5\f\7\2\u0082"+
		"\u0083\7\4\2\2\u0083\u0084\b\t\1\2\u0084\u0086\3\2\2\2\u0085{\3\2\2\2"+
		"\u0085}\3\2\2\2\u0085\u0080\3\2\2\2\u0086\21\3\2\2\2\u0087\u0088\b\n\1"+
		"\2\u0088\u0089\7\31\2\2\u0089\u008a\b\n\1\2\u008a\u0091\3\2\2\2\u008b"+
		"\u008c\6\n\4\3\u008c\u008d\7\5\2\2\u008d\u008e\7\31\2\2\u008e\u0090\b"+
		"\n\1\2\u008f\u008b\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091"+
		"\u0092\3\2\2\2\u0092\23\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\b\13\1"+
		"\2\u0095\u0096\7\31\2\2\u0096\u00aa\b\13\1\2\u0097\u0098\7\26\2\2\u0098"+
		"\u0099\7\13\2\2\u0099\u009a\7\31\2\2\u009a\u009b\7\6\2\2\u009b\u009c\5"+
		"\24\13\2\u009c\u009d\7\4\2\2\u009d\u009e\b\13\1\2\u009e\u00aa\3\2\2\2"+
		"\u009f\u00a0\7\23\2\2\u00a0\u00a1\7\13\2\2\u00a1\u00a2\7\27\2\2\u00a2"+
		"\u00a3\7\6\2\2\u00a3\u00a4\5\24\13\2\u00a4\u00a5\7\6\2\2\u00a5\u00a6\5"+
		"\f\7\2\u00a6\u00a7\7\4\2\2\u00a7\u00a8\b\13\1\2\u00a8\u00aa\3\2\2\2\u00a9"+
		"\u0094\3\2\2\2\u00a9\u0097\3\2\2\2\u00a9\u009f\3\2\2\2\u00aa\u00b7\3\2"+
		"\2\2\u00ab\u00ac\6\13\5\3\u00ac\u00ad\7\5\2\2\u00ad\u00ae\7\31\2\2\u00ae"+
		"\u00b6\b\13\1\2\u00af\u00b0\6\13\6\3\u00b0\u00b1\7\b\2\2\u00b1\u00b2\5"+
		"\f\7\2\u00b2\u00b3\7\3\2\2\u00b3\u00b4\b\13\1\2\u00b4\u00b6\3\2\2\2\u00b5"+
		"\u00ab\3\2\2\2\u00b5\u00af\3\2\2\2\u00b6\u00b9\3\2\2\2\u00b7\u00b5\3\2"+
		"\2\2\u00b7\u00b8\3\2\2\2\u00b8\25\3\2\2\2\u00b9\u00b7\3\2\2\2\r\"9D[j"+
		"y\u0085\u0091\u00a9\u00b5\u00b7";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}