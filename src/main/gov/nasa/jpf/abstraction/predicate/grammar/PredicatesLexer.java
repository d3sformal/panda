// Generated from src/main/gov/nasa/jpf/abstraction/predicate/grammar/Predicates.g4 by ANTLR 4.0
package gov.nasa.jpf.abstraction.predicate.parser;

	import gov.nasa.jpf.abstraction.predicate.grammar.*;
	import gov.nasa.jpf.abstraction.predicate.grammar.impl.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PredicatesLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__20=1, T__19=2, T__18=3, T__17=4, T__16=5, T__15=6, T__14=7, T__13=8, 
		T__12=9, T__11=10, T__10=11, T__9=12, T__8=13, T__7=14, T__6=15, T__5=16, 
		T__4=17, T__3=18, T__2=19, T__1=20, T__0=21, CONSTANT=22, ID=23, WS=24;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"']'", "')'", "'.'", "','", "'+'", "'['", "'-'", "'*'", "'('", "'not'", 
		"'<'", "'='", "'false'", "'object'", "'method'", "'/'", "'aread'", "'true'", 
		"'static'", "'fread'", "'arr'", "CONSTANT", "ID", "WS"
	};
	public static final String[] ruleNames = {
		"T__20", "T__19", "T__18", "T__17", "T__16", "T__15", "T__14", "T__13", 
		"T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", 
		"T__3", "T__2", "T__1", "T__0", "CONSTANT", "ID", "WS"
	};


	public PredicatesLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Predicates.g4"; }

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
		case 23: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:  skip();  break;
		}
	}

	public static final String _serializedATN =
		"\2\4\32\u00af\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27"+
		"\t\27\4\30\t\30\4\31\t\31\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\27\5\27\u0081\n\27\3\27\3\27\3\27"+
		"\7\27\u0086\n\27\f\27\16\27\u0089\13\27\5\27\u008b\n\27\3\27\5\27\u008e"+
		"\n\27\3\27\3\27\7\27\u0092\n\27\f\27\16\27\u0095\13\27\3\27\3\27\7\27"+
		"\u0099\n\27\f\27\16\27\u009c\13\27\5\27\u009e\n\27\5\27\u00a0\n\27\3\30"+
		"\3\30\7\30\u00a4\n\30\f\30\16\30\u00a7\13\30\3\31\6\31\u00aa\n\31\r\31"+
		"\16\31\u00ab\3\31\3\31\2\32\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t"+
		"\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1"+
		"#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\2\3\2\13\4--//\3\62"+
		";\4--//\3\63;\3\62;\3\62;\5C\\aac|\6\62;C\\aac|\4\13\f\"\"\u00b8\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2"+
		"%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61"+
		"\3\2\2\2\3\63\3\2\2\2\5\65\3\2\2\2\7\67\3\2\2\2\t9\3\2\2\2\13;\3\2\2\2"+
		"\r=\3\2\2\2\17?\3\2\2\2\21A\3\2\2\2\23C\3\2\2\2\25E\3\2\2\2\27I\3\2\2"+
		"\2\31K\3\2\2\2\33M\3\2\2\2\35S\3\2\2\2\37Z\3\2\2\2!a\3\2\2\2#c\3\2\2\2"+
		"%i\3\2\2\2\'n\3\2\2\2)u\3\2\2\2+{\3\2\2\2-\u009f\3\2\2\2/\u00a1\3\2\2"+
		"\2\61\u00a9\3\2\2\2\63\64\7_\2\2\64\4\3\2\2\2\65\66\7+\2\2\66\6\3\2\2"+
		"\2\678\7\60\2\28\b\3\2\2\29:\7.\2\2:\n\3\2\2\2;<\7-\2\2<\f\3\2\2\2=>\7"+
		"]\2\2>\16\3\2\2\2?@\7/\2\2@\20\3\2\2\2AB\7,\2\2B\22\3\2\2\2CD\7*\2\2D"+
		"\24\3\2\2\2EF\7p\2\2FG\7q\2\2GH\7v\2\2H\26\3\2\2\2IJ\7>\2\2J\30\3\2\2"+
		"\2KL\7?\2\2L\32\3\2\2\2MN\7h\2\2NO\7c\2\2OP\7n\2\2PQ\7u\2\2QR\7g\2\2R"+
		"\34\3\2\2\2ST\7q\2\2TU\7d\2\2UV\7l\2\2VW\7g\2\2WX\7e\2\2XY\7v\2\2Y\36"+
		"\3\2\2\2Z[\7o\2\2[\\\7g\2\2\\]\7v\2\2]^\7j\2\2^_\7q\2\2_`\7f\2\2` \3\2"+
		"\2\2ab\7\61\2\2b\"\3\2\2\2cd\7c\2\2de\7t\2\2ef\7g\2\2fg\7c\2\2gh\7f\2"+
		"\2h$\3\2\2\2ij\7v\2\2jk\7t\2\2kl\7w\2\2lm\7g\2\2m&\3\2\2\2no\7u\2\2op"+
		"\7v\2\2pq\7c\2\2qr\7v\2\2rs\7k\2\2st\7e\2\2t(\3\2\2\2uv\7h\2\2vw\7t\2"+
		"\2wx\7g\2\2xy\7c\2\2yz\7f\2\2z*\3\2\2\2{|\7c\2\2|}\7t\2\2}~\7t\2\2~,\3"+
		"\2\2\2\177\u0081\t\2\2\2\u0080\177\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0082"+
		"\3\2\2\2\u0082\u008a\7\62\2\2\u0083\u0087\7\60\2\2\u0084\u0086\t\3\2\2"+
		"\u0085\u0084\3\2\2\2\u0086\u0089\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088"+
		"\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u008a\u0083\3\2\2\2\u008a"+
		"\u008b\3\2\2\2\u008b\u00a0\3\2\2\2\u008c\u008e\t\4\2\2\u008d\u008c\3\2"+
		"\2\2\u008d\u008e\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0093\t\5\2\2\u0090"+
		"\u0092\t\6\2\2\u0091\u0090\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2"+
		"\2\2\u0093\u0094\3\2\2\2\u0094\u009d\3\2\2\2\u0095\u0093\3\2\2\2\u0096"+
		"\u009a\7\60\2\2\u0097\u0099\t\7\2\2\u0098\u0097\3\2\2\2\u0099\u009c\3"+
		"\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009e\3\2\2\2\u009c"+
		"\u009a\3\2\2\2\u009d\u0096\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u00a0\3\2"+
		"\2\2\u009f\u0080\3\2\2\2\u009f\u008d\3\2\2\2\u00a0.\3\2\2\2\u00a1\u00a5"+
		"\t\b\2\2\u00a2\u00a4\t\t\2\2\u00a3\u00a2\3\2\2\2\u00a4\u00a7\3\2\2\2\u00a5"+
		"\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\60\3\2\2\2\u00a7\u00a5\3\2\2"+
		"\2\u00a8\u00aa\t\n\2\2\u00a9\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00a9"+
		"\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ae\b\31\2\2"+
		"\u00ae\62\3\2\2\2\r\2\u0080\u0087\u008a\u008d\u0093\u009a\u009d\u009f"+
		"\u00a5\u00ab";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}