// Generated from src/main/gov/nasa/jpf/abstraction/predicate/parser/PredicateGrammar.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.common.*;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PredicateGrammarParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__13=1, T__12=2, T__11=3, T__10=4, T__9=5, T__8=6, T__7=7, T__6=8, T__5=9, 
		T__4=10, T__3=11, T__2=12, T__1=13, T__0=14, CONSTANT=15, ID=16, WS=17;
	public static final String[] tokenNames = {
		"<INVALID>", "']'", "'.'", "')'", "'+'", "'*'", "'-'", "'['", "'('", "'not'", 
		"'<'", "'='", "'false'", "'/'", "'true'", "CONSTANT", "ID", "WS"
	};
	public static final int
		RULE_predicate = 0, RULE_expression = 1, RULE_term = 2, RULE_factor = 3, 
		RULE_dotpath = 4;
	public static final String[] ruleNames = {
		"predicate", "expression", "term", "factor", "dotpath"
	};

	@Override
	public String getGrammarFileName() { return "PredicateGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public PredicateGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
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
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).exitPredicate(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_predicate);
		try {
			setState(30);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(10); match(14);

						((PredicateContext)_localctx).val =  new Equals(new Constant(1), new Constant(1));
					
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(12); match(12);

						((PredicateContext)_localctx).val =  new Equals(new Constant(0), new Constant(1));
					
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(14); match(9);
				setState(15); match(8);
				setState(16); ((PredicateContext)_localctx).p = predicate();
				setState(17); match(3);

						((PredicateContext)_localctx).val =  new Negation(((PredicateContext)_localctx).p.val);
					
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(20); ((PredicateContext)_localctx).a = expression();
				setState(21); match(11);
				setState(22); ((PredicateContext)_localctx).b = expression();

						((PredicateContext)_localctx).val =  new Equals(((PredicateContext)_localctx).a.val, ((PredicateContext)_localctx).b.val);
					
				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(25); ((PredicateContext)_localctx).a = expression();
				setState(26); match(10);
				setState(27); ((PredicateContext)_localctx).b = expression();

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
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expression);
		try {
			setState(45);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(32); ((ExpressionContext)_localctx).t = term();

						((ExpressionContext)_localctx).val =  ((ExpressionContext)_localctx).t.val;
					
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(35); ((ExpressionContext)_localctx).a = term();
				setState(36); match(4);
				setState(37); ((ExpressionContext)_localctx).b = term();

						((ExpressionContext)_localctx).val =  new Add(((ExpressionContext)_localctx).a.val, ((ExpressionContext)_localctx).b.val);
					
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(40); ((ExpressionContext)_localctx).a = term();
				setState(41); match(6);
				setState(42); ((ExpressionContext)_localctx).b = term();

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
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_term);
		try {
			setState(60);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(47); ((TermContext)_localctx).f = factor();

						((TermContext)_localctx).val =  ((TermContext)_localctx).f.val;
					
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(50); ((TermContext)_localctx).a = factor();
				setState(51); match(5);
				setState(52); ((TermContext)_localctx).b = factor();

						((TermContext)_localctx).val =  new Multiply(_localctx.a.val, _localctx.b.val);
					
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(55); ((TermContext)_localctx).a = factor();
				setState(56); match(13);
				setState(57); ((TermContext)_localctx).b = factor();

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
		public DotpathContext path;
		public ExpressionContext e;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode CONSTANT() { return getToken(PredicateGrammarParser.CONSTANT, 0); }
		public DotpathContext dotpath() {
			return getRuleContext(DotpathContext.class,0);
		}
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).enterFactor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).exitFactor(this);
		}
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_factor);
		try {
			setState(72);
			switch (_input.LA(1)) {
			case CONSTANT:
				enterOuterAlt(_localctx, 1);
				{
				setState(62); ((FactorContext)_localctx).CONSTANT = match(CONSTANT);

						((FactorContext)_localctx).val =  new Constant(Integer.parseInt((((FactorContext)_localctx).CONSTANT!=null?((FactorContext)_localctx).CONSTANT.getText():null)));
					
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(64); ((FactorContext)_localctx).path = dotpath(0);

						((FactorContext)_localctx).val =  ((FactorContext)_localctx).path.val;
					
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 3);
				{
				setState(67); match(8);
				setState(68); ((FactorContext)_localctx).e = expression();
				setState(69); match(3);

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

	public static class DotpathContext extends ParserRuleContext {
		public int _p;
		public AccessPath val;
		public DotpathContext subpath;
		public Token ID;
		public Token field;
		public ExpressionContext e;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ID() { return getToken(PredicateGrammarParser.ID, 0); }
		public DotpathContext dotpath() {
			return getRuleContext(DotpathContext.class,0);
		}
		public DotpathContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public DotpathContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_dotpath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).enterDotpath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateGrammarListener ) ((PredicateGrammarListener)listener).exitDotpath(this);
		}
	}

	public final DotpathContext dotpath(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		DotpathContext _localctx = new DotpathContext(_ctx, _parentState, _p);
		DotpathContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, RULE_dotpath);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(75); ((DotpathContext)_localctx).ID = match(ID);

					((DotpathContext)_localctx).val =  new AccessPath((((DotpathContext)_localctx).ID!=null?((DotpathContext)_localctx).ID.getText():null));
				
			}
			_ctx.stop = _input.LT(-1);
			setState(90);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(88);
					switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
					case 1:
						{
						_localctx = new DotpathContext(_parentctx, _parentState, _p);
						_localctx.subpath = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dotpath);
						setState(78);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(79); match(2);
						setState(80); ((DotpathContext)_localctx).field = match(ID);

						          		((DotpathContext)_localctx).val =  ((DotpathContext)_localctx).subpath.val;
						          		_localctx.val.appendField((((DotpathContext)_localctx).field!=null?((DotpathContext)_localctx).field.getText():null));
						          	
						}
						break;

					case 2:
						{
						_localctx = new DotpathContext(_parentctx, _parentState, _p);
						_localctx.subpath = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dotpath);
						setState(82);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						setState(83); match(7);
						setState(84); ((DotpathContext)_localctx).e = expression();
						setState(85); match(1);

						          		((DotpathContext)_localctx).val =  ((DotpathContext)_localctx).subpath.val;
						          		_localctx.val.appendIndex(((DotpathContext)_localctx).e.val);
						          	
						}
						break;
					}
					} 
				}
				setState(92);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
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
		case 4: return dotpath_sempred((DotpathContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean dotpath_sempred(DotpathContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 2 >= _localctx._p;

		case 1: return 1 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\2\3\23`\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2!\n\2\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\60\n\3\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4?\n\4\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\5\5K\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\7\6[\n\6\f\6\16\6^\13\6\3\6\2\7\2\4\6\b\n\2\2f\2"+
		" \3\2\2\2\4/\3\2\2\2\6>\3\2\2\2\bJ\3\2\2\2\nL\3\2\2\2\f\r\7\20\2\2\r!"+
		"\b\2\1\2\16\17\7\16\2\2\17!\b\2\1\2\20\21\7\13\2\2\21\22\7\n\2\2\22\23"+
		"\5\2\2\2\23\24\7\5\2\2\24\25\b\2\1\2\25!\3\2\2\2\26\27\5\4\3\2\27\30\7"+
		"\r\2\2\30\31\5\4\3\2\31\32\b\2\1\2\32!\3\2\2\2\33\34\5\4\3\2\34\35\7\f"+
		"\2\2\35\36\5\4\3\2\36\37\b\2\1\2\37!\3\2\2\2 \f\3\2\2\2 \16\3\2\2\2 \20"+
		"\3\2\2\2 \26\3\2\2\2 \33\3\2\2\2!\3\3\2\2\2\"#\5\6\4\2#$\b\3\1\2$\60\3"+
		"\2\2\2%&\5\6\4\2&\'\7\6\2\2\'(\5\6\4\2()\b\3\1\2)\60\3\2\2\2*+\5\6\4\2"+
		"+,\7\b\2\2,-\5\6\4\2-.\b\3\1\2.\60\3\2\2\2/\"\3\2\2\2/%\3\2\2\2/*\3\2"+
		"\2\2\60\5\3\2\2\2\61\62\5\b\5\2\62\63\b\4\1\2\63?\3\2\2\2\64\65\5\b\5"+
		"\2\65\66\7\7\2\2\66\67\5\b\5\2\678\b\4\1\28?\3\2\2\29:\5\b\5\2:;\7\17"+
		"\2\2;<\5\b\5\2<=\b\4\1\2=?\3\2\2\2>\61\3\2\2\2>\64\3\2\2\2>9\3\2\2\2?"+
		"\7\3\2\2\2@A\7\21\2\2AK\b\5\1\2BC\5\n\6\2CD\b\5\1\2DK\3\2\2\2EF\7\n\2"+
		"\2FG\5\4\3\2GH\7\5\2\2HI\b\5\1\2IK\3\2\2\2J@\3\2\2\2JB\3\2\2\2JE\3\2\2"+
		"\2K\t\3\2\2\2LM\b\6\1\2MN\7\22\2\2NO\b\6\1\2O\\\3\2\2\2PQ\6\6\2\3QR\7"+
		"\4\2\2RS\7\22\2\2S[\b\6\1\2TU\6\6\3\3UV\7\t\2\2VW\5\4\3\2WX\7\3\2\2XY"+
		"\b\6\1\2Y[\3\2\2\2ZP\3\2\2\2ZT\3\2\2\2[^\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2"+
		"]\13\3\2\2\2^\\\3\2\2\2\b />JZ\\";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}