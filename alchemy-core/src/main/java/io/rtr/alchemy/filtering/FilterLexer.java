// Generated from Filter.g4 by ANTLR 4.2.2
package io.rtr.alchemy.filtering;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class FilterLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, AND=3, OR=4, NOT=5, NUMBER=6, STRING=7, BOOLEAN=8, IDENTIFIER=9, 
		COMPARISON=10, WS=11;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"')'", "'('", "AND", "OR", "NOT", "NUMBER", "STRING", "BOOLEAN", "IDENTIFIER", 
		"COMPARISON", "WS"
	};
	public static final String[] ruleNames = {
		"T__1", "T__0", "AND", "OR", "NOT", "NUMBER", "STRING", "BOOLEAN", "IDENTIFIER", 
		"COMPARISON", "WS"
	};


	public FilterLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Filter.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\rc\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\5\4\"\n\4\3\5\3\5\3\5\5\5"+
		"\'\n\5\3\6\3\6\3\6\3\6\5\6-\n\6\3\7\5\7\60\n\7\3\7\6\7\63\n\7\r\7\16\7"+
		"\64\3\b\3\b\7\b9\n\b\f\b\16\b<\13\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\5\tI\n\t\3\n\3\n\7\nM\n\n\f\n\16\nP\13\n\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\5\13[\n\13\3\f\6\f^\n\f\r\f\16\f_\3\f\3\f\2"+
		"\2\r\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\3\2\b\4\2--//"+
		"\3\2\62;\3\2$$\6\2//C\\aac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"o\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\3\31\3"+
		"\2\2\2\5\33\3\2\2\2\7!\3\2\2\2\t&\3\2\2\2\13,\3\2\2\2\r/\3\2\2\2\17\66"+
		"\3\2\2\2\21H\3\2\2\2\23J\3\2\2\2\25Z\3\2\2\2\27]\3\2\2\2\31\32\7+\2\2"+
		"\32\4\3\2\2\2\33\34\7*\2\2\34\6\3\2\2\2\35\"\7(\2\2\36\37\7c\2\2\37 \7"+
		"p\2\2 \"\7f\2\2!\35\3\2\2\2!\36\3\2\2\2\"\b\3\2\2\2#\'\7~\2\2$%\7q\2\2"+
		"%\'\7t\2\2&#\3\2\2\2&$\3\2\2\2\'\n\3\2\2\2(-\7#\2\2)*\7p\2\2*+\7q\2\2"+
		"+-\7v\2\2,(\3\2\2\2,)\3\2\2\2-\f\3\2\2\2.\60\t\2\2\2/.\3\2\2\2/\60\3\2"+
		"\2\2\60\62\3\2\2\2\61\63\t\3\2\2\62\61\3\2\2\2\63\64\3\2\2\2\64\62\3\2"+
		"\2\2\64\65\3\2\2\2\65\16\3\2\2\2\66:\7$\2\2\679\n\4\2\28\67\3\2\2\29<"+
		"\3\2\2\2:8\3\2\2\2:;\3\2\2\2;=\3\2\2\2<:\3\2\2\2=>\7$\2\2>\20\3\2\2\2"+
		"?@\7v\2\2@A\7t\2\2AB\7w\2\2BI\7g\2\2CD\7h\2\2DE\7c\2\2EF\7n\2\2FG\7u\2"+
		"\2GI\7g\2\2H?\3\2\2\2HC\3\2\2\2I\22\3\2\2\2JN\t\5\2\2KM\t\6\2\2LK\3\2"+
		"\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2O\24\3\2\2\2PN\3\2\2\2Q[\4>@\2RS\7>"+
		"\2\2S[\7@\2\2TU\7#\2\2U[\7?\2\2VW\7>\2\2W[\7?\2\2XY\7@\2\2Y[\7?\2\2ZQ"+
		"\3\2\2\2ZR\3\2\2\2ZT\3\2\2\2ZV\3\2\2\2ZX\3\2\2\2[\26\3\2\2\2\\^\t\7\2"+
		"\2]\\\3\2\2\2^_\3\2\2\2_]\3\2\2\2_`\3\2\2\2`a\3\2\2\2ab\b\f\2\2b\30\3"+
		"\2\2\2\r\2!&,/\64:HNZ_\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}