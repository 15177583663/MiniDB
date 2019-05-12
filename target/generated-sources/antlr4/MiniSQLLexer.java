// Generated from MiniSQL.g4 by ANTLR 4.4
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MiniSQLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__40=1, T__39=2, T__38=3, T__37=4, T__36=5, T__35=6, T__34=7, T__33=8, 
		T__32=9, T__31=10, T__30=11, T__29=12, T__28=13, T__27=14, T__26=15, T__25=16, 
		T__24=17, T__23=18, T__22=19, T__21=20, T__20=21, T__19=22, T__18=23, 
		T__17=24, T__16=25, T__15=26, T__14=27, T__13=28, T__12=29, T__11=30, 
		T__10=31, T__9=32, T__8=33, T__7=34, T__6=35, T__5=36, T__4=37, T__3=38, 
		T__2=39, T__1=40, T__0=41, Number=42, Name=43, String=44, NEWLINE=45, 
		WS=46;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'", "'#'", "'$'", "'%'", "'&'", "'''", 
		"'('", "')'", "'*'", "'+'", "','", "'-'", "'.'"
	};
	public static final String[] ruleNames = {
		"T__40", "T__39", "T__38", "T__37", "T__36", "T__35", "T__34", "T__33", 
		"T__32", "T__31", "T__30", "T__29", "T__28", "T__27", "T__26", "T__25", 
		"T__24", "T__23", "T__22", "T__21", "T__20", "T__19", "T__18", "T__17", 
		"T__16", "T__15", "T__14", "T__13", "T__12", "T__11", "T__10", "T__9", 
		"T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", 
		"Number", "Name", "String", "NEWLINE", "WS"
	};


	public MiniSQLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MiniSQL.g4"; }

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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\60\u01a9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\37\3\37\3 \3 \3 \3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3"+
		"\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3"+
		"$\3$\3$\3%\3%\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3"+
		")\3)\3)\3)\3*\3*\3*\3*\3*\3*\3*\3*\3*\3*\3*\3*\3*\3+\6+\u018b\n+\r+\16"+
		"+\u018c\3,\6,\u0190\n,\r,\16,\u0191\3-\3-\6-\u0196\n-\r-\16-\u0197\3-"+
		"\3-\3.\5.\u019d\n.\3.\3.\5.\u01a1\n.\3/\6/\u01a4\n/\r/\16/\u01a5\3/\3"+
		"/\3\u0197\2\60\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34"+
		"\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60\3\2\5\4\2\60\60\62"+
		";\5\2\62;aac|\5\2\13\f\17\17\"\"\u01ae\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3"+
		"\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2"+
		"\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2"+
		"[\3\2\2\2\2]\3\2\2\2\3_\3\2\2\2\5o\3\2\2\2\7{\3\2\2\2\t\u0080\3\2\2\2"+
		"\13\u0084\3\2\2\2\r\u0091\3\2\2\2\17\u0097\3\2\2\2\21\u00a3\3\2\2\2\23"+
		"\u00a6\3\2\2\2\25\u00a8\3\2\2\2\27\u00ab\3\2\2\2\31\u00b6\3\2\2\2\33\u00c7"+
		"\3\2\2\2\35\u00ce\3\2\2\2\37\u00d3\3\2\2\2!\u00d7\3\2\2\2#\u00dc\3\2\2"+
		"\2%\u00ea\3\2\2\2\'\u00ec\3\2\2\2)\u00f3\3\2\2\2+\u00f5\3\2\2\2-\u00fc"+
		"\3\2\2\2/\u00fe\3\2\2\2\61\u0100\3\2\2\2\63\u0105\3\2\2\2\65\u010a\3\2"+
		"\2\2\67\u0118\3\2\2\29\u011b\3\2\2\2;\u011d\3\2\2\2=\u0124\3\2\2\2?\u0126"+
		"\3\2\2\2A\u0129\3\2\2\2C\u0139\3\2\2\2E\u013c\3\2\2\2G\u0142\3\2\2\2I"+
		"\u0152\3\2\2\2K\u0154\3\2\2\2M\u0158\3\2\2\2O\u0167\3\2\2\2Q\u0173\3\2"+
		"\2\2S\u017c\3\2\2\2U\u018a\3\2\2\2W\u018f\3\2\2\2Y\u0193\3\2\2\2[\u01a0"+
		"\3\2\2\2]\u01a3\3\2\2\2_`\7h\2\2`a\7w\2\2ab\7n\2\2bc\7n\2\2cd\7\"\2\2"+
		"de\7q\2\2ef\7w\2\2fg\7v\2\2gh\7g\2\2hi\7t\2\2ij\7\"\2\2jk\7l\2\2kl\7q"+
		"\2\2lm\7k\2\2mn\7p\2\2n\4\3\2\2\2op\7r\2\2pq\7t\2\2qr\7k\2\2rs\7o\2\2"+
		"st\7c\2\2tu\7t\2\2uv\7{\2\2vw\7\"\2\2wx\7m\2\2xy\7g\2\2yz\7{\2\2z\6\3"+
		"\2\2\2{|\7e\2\2|}\7j\2\2}~\7c\2\2~\177\7t\2\2\177\b\3\2\2\2\u0080\u0081"+
		"\7u\2\2\u0081\u0082\7g\2\2\u0082\u0083\7v\2\2\u0083\n\3\2\2\2\u0084\u0085"+
		"\7w\2\2\u0085\u0086\7u\2\2\u0086\u0087\7g\2\2\u0087\u0088\7\"\2\2\u0088"+
		"\u0089\7f\2\2\u0089\u008a\7c\2\2\u008a\u008b\7v\2\2\u008b\u008c\7c\2\2"+
		"\u008c\u008d\7d\2\2\u008d\u008e\7c\2\2\u008e\u008f\7u\2\2\u008f\u0090"+
		"\7g\2\2\u0090\f\3\2\2\2\u0091\u0092\7h\2\2\u0092\u0093\7n\2\2\u0093\u0094"+
		"\7q\2\2\u0094\u0095\7c\2\2\u0095\u0096\7v\2\2\u0096\16\3\2\2\2\u0097\u0098"+
		"\7f\2\2\u0098\u0099\7g\2\2\u0099\u009a\7n\2\2\u009a\u009b\7g\2\2\u009b"+
		"\u009c\7v\2\2\u009c\u009d\7g\2\2\u009d\u009e\7\"\2\2\u009e\u009f\7h\2"+
		"\2\u009f\u00a0\7t\2\2\u00a0\u00a1\7q\2\2\u00a1\u00a2\7o\2\2\u00a2\20\3"+
		"\2\2\2\u00a3\u00a4\7q\2\2\u00a4\u00a5\7p\2\2\u00a5\22\3\2\2\2\u00a6\u00a7"+
		"\7?\2\2\u00a7\24\3\2\2\2\u00a8\u00a9\7>\2\2\u00a9\u00aa\7?\2\2\u00aa\26"+
		"\3\2\2\2\u00ab\u00ac\7f\2\2\u00ac\u00ad\7t\2\2\u00ad\u00ae\7q\2\2\u00ae"+
		"\u00af\7r\2\2\u00af\u00b0\7\"\2\2\u00b0\u00b1\7v\2\2\u00b1\u00b2\7c\2"+
		"\2\u00b2\u00b3\7d\2\2\u00b3\u00b4\7n\2\2\u00b4\u00b5\7g\2\2\u00b5\30\3"+
		"\2\2\2\u00b6\u00b7\7t\2\2\u00b7\u00b8\7k\2\2\u00b8\u00b9\7i\2\2\u00b9"+
		"\u00ba\7j\2\2\u00ba\u00bb\7v\2\2\u00bb\u00bc\7\"\2\2\u00bc\u00bd\7q\2"+
		"\2\u00bd\u00be\7w\2\2\u00be\u00bf\7v\2\2\u00bf\u00c0\7g\2\2\u00c0\u00c1"+
		"\7t\2\2\u00c1\u00c2\7\"\2\2\u00c2\u00c3\7l\2\2\u00c3\u00c4\7q\2\2\u00c4"+
		"\u00c5\7k\2\2\u00c5\u00c6\7p\2\2\u00c6\32\3\2\2\2\u00c7\u00c8\7f\2\2\u00c8"+
		"\u00c9\7q\2\2\u00c9\u00ca\7w\2\2\u00ca\u00cb\7d\2\2\u00cb\u00cc\7n\2\2"+
		"\u00cc\u00cd\7g\2\2\u00cd\34\3\2\2\2\u00ce\u00cf\7h\2\2\u00cf\u00d0\7"+
		"t\2\2\u00d0\u00d1\7q\2\2\u00d1\u00d2\7o\2\2\u00d2\36\3\2\2\2\u00d3\u00d4"+
		"\7k\2\2\u00d4\u00d5\7p\2\2\u00d5\u00d6\7v\2\2\u00d6 \3\2\2\2\u00d7\u00d8"+
		"\7p\2\2\u00d8\u00d9\7w\2\2\u00d9\u00da\7n\2\2\u00da\u00db\7n\2\2\u00db"+
		"\"\3\2\2\2\u00dc\u00dd\7f\2\2\u00dd\u00de\7t\2\2\u00de\u00df\7q\2\2\u00df"+
		"\u00e0\7r\2\2\u00e0\u00e1\7\"\2\2\u00e1\u00e2\7f\2\2\u00e2\u00e3\7c\2"+
		"\2\u00e3\u00e4\7v\2\2\u00e4\u00e5\7c\2\2\u00e5\u00e6\7d\2\2\u00e6\u00e7"+
		"\7c\2\2\u00e7\u00e8\7u\2\2\u00e8\u00e9\7g\2\2\u00e9$\3\2\2\2\u00ea\u00eb"+
		"\7*\2\2\u00eb&\3\2\2\2\u00ec\u00ed\7x\2\2\u00ed\u00ee\7c\2\2\u00ee\u00ef"+
		"\7n\2\2\u00ef\u00f0\7w\2\2\u00f0\u00f1\7g\2\2\u00f1\u00f2\7u\2\2\u00f2"+
		"(\3\2\2\2\u00f3\u00f4\7,\2\2\u00f4*\3\2\2\2\u00f5\u00f6\7w\2\2\u00f6\u00f7"+
		"\7r\2\2\u00f7\u00f8\7f\2\2\u00f8\u00f9\7c\2\2\u00f9\u00fa\7v\2\2\u00fa"+
		"\u00fb\7g\2\2\u00fb,\3\2\2\2\u00fc\u00fd\7.\2\2\u00fd.\3\2\2\2\u00fe\u00ff"+
		"\7\60\2\2\u00ff\60\3\2\2\2\u0100\u0101\7l\2\2\u0101\u0102\7q\2\2\u0102"+
		"\u0103\7k\2\2\u0103\u0104\7p\2\2\u0104\62\3\2\2\2\u0105\u0106\7n\2\2\u0106"+
		"\u0107\7q\2\2\u0107\u0108\7p\2\2\u0108\u0109\7i\2\2\u0109\64\3\2\2\2\u010a"+
		"\u010b\7u\2\2\u010b\u010c\7j\2\2\u010c\u010d\7q\2\2\u010d\u010e\7y\2\2"+
		"\u010e\u010f\7\"\2\2\u010f\u0110\7f\2\2\u0110\u0111\7c\2\2\u0111\u0112"+
		"\7v\2\2\u0112\u0113\7c\2\2\u0113\u0114\7d\2\2\u0114\u0115\7c\2\2\u0115"+
		"\u0116\7u\2\2\u0116\u0117\7g\2\2\u0117\66\3\2\2\2\u0118\u0119\7@\2\2\u0119"+
		"\u011a\7?\2\2\u011a8\3\2\2\2\u011b\u011c\7>\2\2\u011c:\3\2\2\2\u011d\u011e"+
		"\7u\2\2\u011e\u011f\7g\2\2\u011f\u0120\7n\2\2\u0120\u0121\7g\2\2\u0121"+
		"\u0122\7e\2\2\u0122\u0123\7v\2\2\u0123<\3\2\2\2\u0124\u0125\7@\2\2\u0125"+
		">\3\2\2\2\u0126\u0127\7q\2\2\u0127\u0128\7t\2\2\u0128@\3\2\2\2\u0129\u012a"+
		"\7n\2\2\u012a\u012b\7g\2\2\u012b\u012c\7h\2\2\u012c\u012d\7v\2\2\u012d"+
		"\u012e\7\"\2\2\u012e\u012f\7q\2\2\u012f\u0130\7w\2\2\u0130\u0131\7v\2"+
		"\2\u0131\u0132\7g\2\2\u0132\u0133\7t\2\2\u0133\u0134\7\"\2\2\u0134\u0135"+
		"\7l\2\2\u0135\u0136\7q\2\2\u0136\u0137\7k\2\2\u0137\u0138\7p\2\2\u0138"+
		"B\3\2\2\2\u0139\u013a\7>\2\2\u013a\u013b\7@\2\2\u013bD\3\2\2\2\u013c\u013d"+
		"\7y\2\2\u013d\u013e\7j\2\2\u013e\u013f\7g\2\2\u013f\u0140\7t\2\2\u0140"+
		"\u0141\7g\2\2\u0141F\3\2\2\2\u0142\u0143\7e\2\2\u0143\u0144\7t\2\2\u0144"+
		"\u0145\7g\2\2\u0145\u0146\7c\2\2\u0146\u0147\7v\2\2\u0147\u0148\7g\2\2"+
		"\u0148\u0149\7\"\2\2\u0149\u014a\7f\2\2\u014a\u014b\7c\2\2\u014b\u014c"+
		"\7v\2\2\u014c\u014d\7c\2\2\u014d\u014e\7d\2\2\u014e\u014f\7c\2\2\u014f"+
		"\u0150\7u\2\2\u0150\u0151\7g\2\2\u0151H\3\2\2\2\u0152\u0153\7+\2\2\u0153"+
		"J\3\2\2\2\u0154\u0155\7c\2\2\u0155\u0156\7p\2\2\u0156\u0157\7f\2\2\u0157"+
		"L\3\2\2\2\u0158\u0159\7u\2\2\u0159\u015a\7j\2\2\u015a\u015b\7q\2\2\u015b"+
		"\u015c\7y\2\2\u015c\u015d\7\"\2\2\u015d\u015e\7f\2\2\u015e\u015f\7c\2"+
		"\2\u015f\u0160\7v\2\2\u0160\u0161\7c\2\2\u0161\u0162\7d\2\2\u0162\u0163"+
		"\7c\2\2\u0163\u0164\7u\2\2\u0164\u0165\7g\2\2\u0165\u0166\7u\2\2\u0166"+
		"N\3\2\2\2\u0167\u0168\7k\2\2\u0168\u0169\7p\2\2\u0169\u016a\7u\2\2\u016a"+
		"\u016b\7g\2\2\u016b\u016c\7t\2\2\u016c\u016d\7v\2\2\u016d\u016e\7\"\2"+
		"\2\u016e\u016f\7k\2\2\u016f\u0170\7p\2\2\u0170\u0171\7v\2\2\u0171\u0172"+
		"\7q\2\2\u0172P\3\2\2\2\u0173\u0174\7p\2\2\u0174\u0175\7q\2\2\u0175\u0176"+
		"\7v\2\2\u0176\u0177\7\"\2\2\u0177\u0178\7p\2\2\u0178\u0179\7w\2\2\u0179"+
		"\u017a\7n\2\2\u017a\u017b\7n\2\2\u017bR\3\2\2\2\u017c\u017d\7e\2\2\u017d"+
		"\u017e\7t\2\2\u017e\u017f\7g\2\2\u017f\u0180\7c\2\2\u0180\u0181\7v\2\2"+
		"\u0181\u0182\7g\2\2\u0182\u0183\7\"\2\2\u0183\u0184\7v\2\2\u0184\u0185"+
		"\7c\2\2\u0185\u0186\7d\2\2\u0186\u0187\7n\2\2\u0187\u0188\7g\2\2\u0188"+
		"T\3\2\2\2\u0189\u018b\t\2\2\2\u018a\u0189\3\2\2\2\u018b\u018c\3\2\2\2"+
		"\u018c\u018a\3\2\2\2\u018c\u018d\3\2\2\2\u018dV\3\2\2\2\u018e\u0190\t"+
		"\3\2\2\u018f\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u018f\3\2\2\2\u0191"+
		"\u0192\3\2\2\2\u0192X\3\2\2\2\u0193\u0195\7)\2\2\u0194\u0196\13\2\2\2"+
		"\u0195\u0194\3\2\2\2\u0196\u0197\3\2\2\2\u0197\u0198\3\2\2\2\u0197\u0195"+
		"\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019a\7)\2\2\u019aZ\3\2\2\2\u019b\u019d"+
		"\7\17\2\2\u019c\u019b\3\2\2\2\u019c\u019d\3\2\2\2\u019d\u019e\3\2\2\2"+
		"\u019e\u01a1\7\f\2\2\u019f\u01a1\7\2\2\3\u01a0\u019c\3\2\2\2\u01a0\u019f"+
		"\3\2\2\2\u01a1\\\3\2\2\2\u01a2\u01a4\t\4\2\2\u01a3\u01a2\3\2\2\2\u01a4"+
		"\u01a5\3\2\2\2\u01a5\u01a3\3\2\2\2\u01a5\u01a6\3\2\2\2\u01a6\u01a7\3\2"+
		"\2\2\u01a7\u01a8\b/\2\2\u01a8^\3\2\2\2\t\2\u018c\u0191\u0197\u019c\u01a0"+
		"\u01a5\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}