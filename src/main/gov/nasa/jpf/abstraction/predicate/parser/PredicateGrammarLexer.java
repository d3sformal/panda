// Generated from src/main/gov/nasa/jpf/abstraction/predicate/parser/PredicateGrammar.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.common.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PredicateGrammarLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__17=1, T__16=2, T__15=3, T__14=4, T__13=5, T__12=6, T__11=7, T__10=8, 
		T__9=9, T__8=10, T__7=11, T__6=12, T__5=13, T__4=14, T__3=15, T__2=16, 
		T__1=17, T__0=18, CONSTANT=19, ID=20, WS=21;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"']'", "'.'", "')'", "','", "'+'", "'['", "'*'", "'-'", "'('", "'not'", 
		"'<'", "'='", "'false'", "'/'", "'aread'", "'true'", "'fread'", "'arr'", 
		"CONSTANT", "ID", "WS"
	};
	public static final String[] ruleNames = {
		"T__17", "T__16", "T__15", "T__14", "T__13", "T__12", "T__11", "T__10", 
		"T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", 
		"T__0", "CONSTANT", "ID", "WS"
	};


	public PredicateGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PredicateGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 20: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:  skip();  break;
		}
	}

	public static final String _serializedATN =
		"\2\4\27\u0094\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17"+
		"\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\5\24f\n\24\3\24\3\24\3\24"+
		"\7\24k\n\24\f\24\16\24n\13\24\5\24p\n\24\3\24\5\24s\n\24\3\24\3\24\7\24"+
		"w\n\24\f\24\16\24z\13\24\3\24\3\24\7\24~\n\24\f\24\16\24\u0081\13\24\5"+
		"\24\u0083\n\24\5\24\u0085\n\24\3\25\3\25\7\25\u0089\n\25\f\25\16\25\u008c"+
		"\13\25\3\26\6\26\u008f\n\26\r\26\16\26\u0090\3\26\3\26\2\27\3\3\1\5\4"+
		"\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16"+
		"\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\2\3\2\n"+
		"\4--//\3\62;\4--//\3\63;\3\62;\3\62;\5C\\aac|\6\62;C\\aac|\u009d\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2"+
		"%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\3-\3\2\2\2\5/\3\2\2\2\7\61"+
		"\3\2\2\2\t\63\3\2\2\2\13\65\3\2\2\2\r\67\3\2\2\2\179\3\2\2\2\21;\3\2\2"+
		"\2\23=\3\2\2\2\25?\3\2\2\2\27C\3\2\2\2\31E\3\2\2\2\33G\3\2\2\2\35M\3\2"+
		"\2\2\37O\3\2\2\2!U\3\2\2\2#Z\3\2\2\2%`\3\2\2\2\'\u0084\3\2\2\2)\u0086"+
		"\3\2\2\2+\u008e\3\2\2\2-.\7_\2\2.\4\3\2\2\2/\60\7\60\2\2\60\6\3\2\2\2"+
		"\61\62\7+\2\2\62\b\3\2\2\2\63\64\7.\2\2\64\n\3\2\2\2\65\66\7-\2\2\66\f"+
		"\3\2\2\2\678\7]\2\28\16\3\2\2\29:\7,\2\2:\20\3\2\2\2;<\7/\2\2<\22\3\2"+
		"\2\2=>\7*\2\2>\24\3\2\2\2?@\7p\2\2@A\7q\2\2AB\7v\2\2B\26\3\2\2\2CD\7>"+
		"\2\2D\30\3\2\2\2EF\7?\2\2F\32\3\2\2\2GH\7h\2\2HI\7c\2\2IJ\7n\2\2JK\7u"+
		"\2\2KL\7g\2\2L\34\3\2\2\2MN\7\61\2\2N\36\3\2\2\2OP\7c\2\2PQ\7t\2\2QR\7"+
		"g\2\2RS\7c\2\2ST\7f\2\2T \3\2\2\2UV\7v\2\2VW\7t\2\2WX\7w\2\2XY\7g\2\2"+
		"Y\"\3\2\2\2Z[\7h\2\2[\\\7t\2\2\\]\7g\2\2]^\7c\2\2^_\7f\2\2_$\3\2\2\2`"+
		"a\7c\2\2ab\7t\2\2bc\7t\2\2c&\3\2\2\2df\t\2\2\2ed\3\2\2\2ef\3\2\2\2fg\3"+
		"\2\2\2go\7\62\2\2hl\7\60\2\2ik\t\3\2\2ji\3\2\2\2kn\3\2\2\2lj\3\2\2\2l"+
		"m\3\2\2\2mp\3\2\2\2nl\3\2\2\2oh\3\2\2\2op\3\2\2\2p\u0085\3\2\2\2qs\t\4"+
		"\2\2rq\3\2\2\2rs\3\2\2\2st\3\2\2\2tx\t\5\2\2uw\t\6\2\2vu\3\2\2\2wz\3\2"+
		"\2\2xv\3\2\2\2xy\3\2\2\2y\u0082\3\2\2\2zx\3\2\2\2{\177\7\60\2\2|~\t\7"+
		"\2\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080\3\2\2\2\u0080\u0083"+
		"\3\2\2\2\u0081\177\3\2\2\2\u0082{\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085"+
		"\3\2\2\2\u0084e\3\2\2\2\u0084r\3\2\2\2\u0085(\3\2\2\2\u0086\u008a\t\b"+
		"\2\2\u0087\u0089\t\t\2\2\u0088\u0087\3\2\2\2\u0089\u008c\3\2\2\2\u008a"+
		"\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b*\3\2\2\2\u008c\u008a\3\2\2\2"+
		"\u008d\u008f\7\"\2\2\u008e\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u008e"+
		"\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0093\b\26\2\2"+
		"\u0093,\3\2\2\2\r\2elorx\177\u0082\u0084\u008a\u0090";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}