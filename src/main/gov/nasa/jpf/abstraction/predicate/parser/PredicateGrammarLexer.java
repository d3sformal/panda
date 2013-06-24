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
		T__13=1, T__12=2, T__11=3, T__10=4, T__9=5, T__8=6, T__7=7, T__6=8, T__5=9, 
		T__4=10, T__3=11, T__2=12, T__1=13, T__0=14, CONSTANT=15, ID=16, WS=17;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"']'", "'.'", "')'", "'+'", "'*'", "'-'", "'['", "'('", "'not'", "'<'", 
		"'='", "'false'", "'/'", "'true'", "CONSTANT", "ID", "WS"
	};
	public static final String[] ruleNames = {
		"T__13", "T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", 
		"T__4", "T__3", "T__2", "T__1", "T__0", "CONSTANT", "ID", "WS"
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
		case 16: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:  skip();  break;
		}
	}

	public static final String _serializedATN =
		"\2\4\23z\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4"+
		"\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20"+
		"\4\21\t\21\4\22\t\22\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\5\20L\n\20\3\20\3\20\3\20\7"+
		"\20Q\n\20\f\20\16\20T\13\20\5\20V\n\20\3\20\5\20Y\n\20\3\20\3\20\7\20"+
		"]\n\20\f\20\16\20`\13\20\3\20\3\20\7\20d\n\20\f\20\16\20g\13\20\5\20i"+
		"\n\20\5\20k\n\20\3\21\3\21\7\21o\n\21\f\21\16\21r\13\21\3\22\6\22u\n\22"+
		"\r\22\16\22v\3\22\3\22\2\23\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t"+
		"\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1"+
		"#\23\2\3\2\n\4--//\3\62;\4--//\3\63;\3\62;\3\62;\5C\\aac|\6\62;C\\aac"+
		"|\u0083\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3"+
		"\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2"+
		"\2#\3\2\2\2\3%\3\2\2\2\5\'\3\2\2\2\7)\3\2\2\2\t+\3\2\2\2\13-\3\2\2\2\r"+
		"/\3\2\2\2\17\61\3\2\2\2\21\63\3\2\2\2\23\65\3\2\2\2\259\3\2\2\2\27;\3"+
		"\2\2\2\31=\3\2\2\2\33C\3\2\2\2\35E\3\2\2\2\37j\3\2\2\2!l\3\2\2\2#t\3\2"+
		"\2\2%&\7_\2\2&\4\3\2\2\2\'(\7\60\2\2(\6\3\2\2\2)*\7+\2\2*\b\3\2\2\2+,"+
		"\7-\2\2,\n\3\2\2\2-.\7,\2\2.\f\3\2\2\2/\60\7/\2\2\60\16\3\2\2\2\61\62"+
		"\7]\2\2\62\20\3\2\2\2\63\64\7*\2\2\64\22\3\2\2\2\65\66\7p\2\2\66\67\7"+
		"q\2\2\678\7v\2\28\24\3\2\2\29:\7>\2\2:\26\3\2\2\2;<\7?\2\2<\30\3\2\2\2"+
		"=>\7h\2\2>?\7c\2\2?@\7n\2\2@A\7u\2\2AB\7g\2\2B\32\3\2\2\2CD\7\61\2\2D"+
		"\34\3\2\2\2EF\7v\2\2FG\7t\2\2GH\7w\2\2HI\7g\2\2I\36\3\2\2\2JL\t\2\2\2"+
		"KJ\3\2\2\2KL\3\2\2\2LM\3\2\2\2MU\7\62\2\2NR\7\60\2\2OQ\t\3\2\2PO\3\2\2"+
		"\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2SV\3\2\2\2TR\3\2\2\2UN\3\2\2\2UV\3\2\2"+
		"\2Vk\3\2\2\2WY\t\4\2\2XW\3\2\2\2XY\3\2\2\2YZ\3\2\2\2Z^\t\5\2\2[]\t\6\2"+
		"\2\\[\3\2\2\2]`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_h\3\2\2\2`^\3\2\2\2ae\7\60"+
		"\2\2bd\t\7\2\2cb\3\2\2\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2\2fi\3\2\2\2ge\3\2"+
		"\2\2ha\3\2\2\2hi\3\2\2\2ik\3\2\2\2jK\3\2\2\2jX\3\2\2\2k \3\2\2\2lp\t\b"+
		"\2\2mo\t\t\2\2nm\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2q\"\3\2\2\2rp\3"+
		"\2\2\2su\7\"\2\2ts\3\2\2\2uv\3\2\2\2vt\3\2\2\2vw\3\2\2\2wx\3\2\2\2xy\b"+
		"\22\2\2y$\3\2\2\2\r\2KRUX^ehjpv";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}