// Generated from Filter.g4 by ANTLR 4.5
package io.rtr.alchemy.filtering;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FilterParser}.
 */
public interface FilterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link FilterParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(FilterParser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(FilterParser.ExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link FilterParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(FilterParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(FilterParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link FilterParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(FilterParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(FilterParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link FilterParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(FilterParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(FilterParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link FilterParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(FilterParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(FilterParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link FilterParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(FilterParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link FilterParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(FilterParser.ValueContext ctx);
}