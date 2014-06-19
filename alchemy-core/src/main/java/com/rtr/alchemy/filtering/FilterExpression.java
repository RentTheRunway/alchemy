package com.rtr.alchemy.filtering;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Set;
import java.util.Stack;

/**
 * Allows evaluation of a filter expression which tests whether given attributes are present
 */
public class FilterExpression {
    private final ParseTreeWalker walker;
    private final FilterParser.ExpContext expression;

    public FilterExpression(String expression) {
        final ANTLRInputStream inputStream = new ANTLRInputStream(expression);
        final FilterLexer lexer = new FilterLexer(inputStream);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        final FilterParser parser = new FilterParser(tokenStream);
        this.expression = parser.exp();
        this.walker = new ParseTreeWalker();
    }

    /**
     * Evaluates the filter
     * @param attributes Attributes to evaluate against
     */
    public boolean evaluate(Set<String> attributes) {
        final FilterExpressionEvaluator evaluator = new FilterExpressionEvaluator(attributes);
        walker.walk(evaluator, expression);
        return evaluator.getResult();
    }

    /**
     * A parser listener that evaluates the boolean logic expression by traversing the tree
     * Each time an operation is encountered (e.g. exp, factor or term), we push onto the stack
     * OP to serve as a place holder to separate contiguous values that should be applied to an
     * operation.
     */
    private static class FilterExpressionEvaluator implements FilterListener {
        private static final Boolean OP = null;
        private final Stack<Boolean> stack;
        private final Set<String> attributes;

        public FilterExpressionEvaluator(Set<String> attributes) {
            this.stack = new Stack<>();
            this.attributes = attributes;
        }

        public boolean getResult() {
            return stack.peek();
        }

        @Override
        public void enterExp(@NotNull FilterParser.ExpContext ctx) {
            stack.push(OP);
        }

        @Override
        public void exitExp(@NotNull FilterParser.ExpContext ctx) {
            boolean accum = stack.pop();

            while (!stack.empty()) {
                Boolean nextValue = stack.pop();
                if (nextValue == OP) {
                    break;
                }
                accum |= nextValue;
            }

            stack.push(accum);
        }

        @Override
        public void enterTerm(@NotNull FilterParser.TermContext ctx) {
            stack.push(OP);
        }

        @Override
        public void exitTerm(@NotNull FilterParser.TermContext ctx) {
            boolean accum = stack.pop();

            while (!stack.empty()) {
                Boolean nextValue = stack.pop();
                if (nextValue == OP) {
                    break;
                }
                accum &= nextValue;
            }

            stack.push(accum);
        }

        @Override
        public void enterFactor(@NotNull FilterParser.FactorContext ctx) {
            stack.push(OP);
        }

        @Override
        public void exitFactor(@NotNull FilterParser.FactorContext ctx) {
            boolean value = stack.pop();
            stack.pop();
            stack.push(ctx.NOT() != null ? !value : value);
        }

        @Override
        public void enterValue(@NotNull FilterParser.ValueContext ctx) {
        }

        @Override
        public void exitValue(@NotNull FilterParser.ValueContext ctx) {
            stack.push(attributes.contains(ctx.getText()));
        }

        @Override
        public void visitTerminal(@NotNull TerminalNode terminalNode) {

        }

        @Override
        public void visitErrorNode(@NotNull ErrorNode errorNode) {

        }

        @Override
        public void enterEveryRule(@NotNull ParserRuleContext parserRuleContext) {

        }

        @Override
        public void exitEveryRule(@NotNull ParserRuleContext parserRuleContext) {
        }
    }
}
