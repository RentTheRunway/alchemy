// Generated from Filter.g4 by ANTLR 4.2.2
package io.rtr.alchemy.filtering;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FilterParser}.
 */
public interface FilterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link FilterParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(@NotNull FilterParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(@NotNull FilterParser.ComparisonContext ctx);

	/**
	 * Enter a parse tree produced by {@link FilterParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(@NotNull FilterParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(@NotNull FilterParser.ConstantContext ctx);

	/**
	 * Enter a parse tree produced by {@link FilterParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(@NotNull FilterParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(@NotNull FilterParser.TermContext ctx);

	/**
	 * Enter a parse tree produced by {@link FilterParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(@NotNull FilterParser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(@NotNull FilterParser.ExpContext ctx);

	/**
	 * Enter a parse tree produced by {@link FilterParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(@NotNull FilterParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(@NotNull FilterParser.FactorContext ctx);

	/**
	 * Enter a parse tree produced by {@link FilterParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(@NotNull FilterParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(@NotNull FilterParser.ValueContext ctx);
}