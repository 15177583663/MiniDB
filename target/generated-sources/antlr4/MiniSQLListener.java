// Generated from MiniSQL.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MiniSQLParser}.
 */
public interface MiniSQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MiniSQLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MiniSQLParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSQLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MiniSQLParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code create}
	 * labeled alternative in {@link MiniSQLParser#sql}.
	 * @param ctx the parse tree
	 */
	void enterCreate(MiniSQLParser.CreateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code create}
	 * labeled alternative in {@link MiniSQLParser#sql}.
	 * @param ctx the parse tree
	 */
	void exitCreate(MiniSQLParser.CreateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code drop}
	 * labeled alternative in {@link MiniSQLParser#sql}.
	 * @param ctx the parse tree
	 */
	void enterDrop(MiniSQLParser.DropContext ctx);
	/**
	 * Exit a parse tree produced by the {@code drop}
	 * labeled alternative in {@link MiniSQLParser#sql}.
	 * @param ctx the parse tree
	 */
	void exitDrop(MiniSQLParser.DropContext ctx);
	/**
	 * Enter a parse tree produced by the {@code attrcons}
	 * labeled alternative in {@link MiniSQLParser#schema}.
	 * @param ctx the parse tree
	 */
	void enterAttrcons(MiniSQLParser.AttrconsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code attrcons}
	 * labeled alternative in {@link MiniSQLParser#schema}.
	 * @param ctx the parse tree
	 */
	void exitAttrcons(MiniSQLParser.AttrconsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code normalattr}
	 * labeled alternative in {@link MiniSQLParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterNormalattr(MiniSQLParser.NormalattrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code normalattr}
	 * labeled alternative in {@link MiniSQLParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitNormalattr(MiniSQLParser.NormalattrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notnullattr}
	 * labeled alternative in {@link MiniSQLParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterNotnullattr(MiniSQLParser.NotnullattrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notnullattr}
	 * labeled alternative in {@link MiniSQLParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitNotnullattr(MiniSQLParser.NotnullattrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code primarykey}
	 * labeled alternative in {@link MiniSQLParser#constraint}.
	 * @param ctx the parse tree
	 */
	void enterPrimarykey(MiniSQLParser.PrimarykeyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code primarykey}
	 * labeled alternative in {@link MiniSQLParser#constraint}.
	 * @param ctx the parse tree
	 */
	void exitPrimarykey(MiniSQLParser.PrimarykeyContext ctx);
}