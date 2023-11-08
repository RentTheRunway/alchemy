package io.rtr.alchemy.filtering;

import com.google.common.base.Objects;

import io.rtr.alchemy.identities.AttributesMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;

/** Allows evaluation of a filter expression which tests whether given attributes are present */
public class FilterExpression {
    private static final FilterExpression ALWAYS_TRUE = new FilterExpression();
    private static final FilterErrorListener ERROR_LISTENER = new FilterErrorListener();
    private final ParseTreeWalker walker;
    private final FilterParser.ExpContext expression;
    private final String expressionString;

    private FilterExpression(String expressionString, FilterParser parser) {
        this.expression = parser.exp();
        this.walker = new ParseTreeWalker();
        this.expressionString = expressionString;
    }

    private FilterExpression() {
        this.walker = null;
        this.expression = null;
        this.expressionString = "true";
    }

    public static FilterExpression alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public static FilterExpression of(String expression) {
        if (expression.equals("true")) {
            return ALWAYS_TRUE;
        }

        final ANTLRInputStream inputStream = new ANTLRInputStream(expression);
        final FilterLexer lexer = new FilterLexer(inputStream);
        lexer.getErrorListeners().clear();
        lexer.addErrorListener(ERROR_LISTENER);

        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        final FilterParser parser = new FilterParser(tokenStream);
        parser.getErrorListeners().clear();
        parser.addErrorListener(ERROR_LISTENER);

        return new FilterExpression(expression, parser);
    }

    /**
     * Evaluates the filter
     *
     * @param attributes Attributes to evaluate against
     */
    public boolean evaluate(AttributesMap attributes) {
        if (expression == null) {
            return true;
        }

        final FilterExpressionEvaluator evaluator = new FilterExpressionEvaluator(attributes);
        walker.walk(evaluator, expression);
        return evaluator.getResult();
    }

    @Override
    public String toString() {
        return expressionString;
    }

    public static boolean isValid(String expression) {
        try {
            FilterExpression.of(expression);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FilterExpression)) {
            return false;
        }

        final FilterExpression other = (FilterExpression) obj;
        return Objects.equal(expressionString, other.expressionString);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expressionString);
    }

    /**
     * A parser listener that evaluates the boolean logic expression by traversing the tree Each
     * time an operation is encountered (e.g. exp, factor or term), we push onto the stack OP to
     * serve as a place holder to separate contiguous values that should be applied to an operation.
     * Comparisons are evaluated when exiting a comparison, the boolean value of which is then
     * pushed onto the stack as well
     */
    private static class FilterExpressionEvaluator implements FilterListener {
        private static final Boolean OP = null;
        private final Stack<Boolean> stack;
        private final AttributesMap attributes;

        public FilterExpressionEvaluator(AttributesMap attributes) {
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
            // do an OR of values on stack
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
            // do an AND of values on stack
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
            if (ctx.value() != null) {
                // was a singular value, need to evaluate it
                stack.pop();

                final FilterParser.ValueContext context = ctx.value();
                // singular value, either an identifier or constant
                if (context.IDENTIFIER() != null) {
                    // identifier
                    final String name = context.getText();
                    final Class<?> type = attributes.getType(name);

                    if (type == String.class) {
                        stack.push(!attributes.getString(name).isEmpty());
                    } else if (type == Long.class) {
                        stack.push(attributes.getNumber(name) != 0);
                    } else if (type == Boolean.class) {
                        stack.push(attributes.getBoolean(name));
                    } else {
                        stack.push(false);
                    }
                } else {
                    // constant value
                    final FilterParser.ConstantContext constant = context.constant();
                    if (constant.STRING() != null) {
                        // empty string is false, otherwise true
                        stack.push(constant.STRING().getText().length() > 2);
                    } else if (constant.NUMBER() != null) {
                        // 0 = false, all others = true
                        final long value = Long.parseLong(constant.NUMBER().getText());
                        stack.push(value != 0);
                    } else if (constant.BOOLEAN() != null) {
                        // direct conversion
                        stack.push(Boolean.parseBoolean(constant.BOOLEAN().getText()));
                    } else {
                        stack.push(false);
                    }
                }
            } else {
                // was something else, which has already been evaluated to a boolean
                final boolean value = stack.pop();
                stack.pop();
                stack.push(ctx.NOT() != null ? !value : value);
            }
        }

        @Override
        public void enterValue(@NotNull FilterParser.ValueContext ctx) {}

        @Override
        public void exitValue(@NotNull FilterParser.ValueContext ctx) {}

        @Override
        public void visitTerminal(@NotNull TerminalNode terminalNode) {}

        @Override
        public void visitErrorNode(@NotNull ErrorNode errorNode) {}

        @Override
        public void enterEveryRule(@NotNull ParserRuleContext parserRuleContext) {}

        @Override
        public void exitEveryRule(@NotNull ParserRuleContext parserRuleContext) {}

        @Override
        public void enterConstant(@NotNull FilterParser.ConstantContext ctx) {}

        @Override
        public void exitConstant(@NotNull FilterParser.ConstantContext ctx) {}

        @Override
        public void enterComparison(@NotNull FilterParser.ComparisonContext ctx) {}

        private Class<?> getType(FilterParser.ValueContext context) {
            if (context.IDENTIFIER() != null) {
                // identifier
                return attributes.getType(context.IDENTIFIER().getText());
            } else {
                // constant
                final FilterParser.ConstantContext constant = context.constant();
                if (constant.STRING() != null) {
                    return String.class;
                } else if (constant.NUMBER() != null) {
                    return Long.class;
                } else if (constant.BOOLEAN() != null) {
                    return Boolean.class;
                }

                return null;
            }
        }

        private Comparable getValue(FilterParser.ValueContext context) {
            if (context.IDENTIFIER() != null) {
                // identifier
                final String identifier = context.IDENTIFIER().getText();
                final Class<?> type = attributes.getType(identifier);
                if (type == String.class) {
                    return attributes.getString(identifier);
                } else if (type == Long.class) {
                    return attributes.getNumber(identifier);
                } else if (type == Boolean.class) {
                    return attributes.getBoolean(identifier);
                }

                return null;
            } else {
                // constant
                final FilterParser.ConstantContext constant = context.constant();
                if (constant.STRING() != null) {
                    final String value = constant.STRING().getText();
                    return value.length() == 2 ? "" : value.substring(1, value.length() - 1);
                } else if (constant.NUMBER() != null) {
                    return Long.parseLong(constant.NUMBER().getText());
                } else if (constant.BOOLEAN() != null) {
                    return Boolean.parseBoolean(constant.BOOLEAN().getText());
                }

                return null;
            }
        }

        @Override
        public void exitComparison(@NotNull FilterParser.ComparisonContext ctx) {
            final FilterParser.ValueContext lhs = ctx.value(0);
            final FilterParser.ValueContext rhs = ctx.value(1);

            // for comparison, only support comparing like types for now
            final Class<?> lhsType = getType(lhs);
            final Class<?> rhsType = getType(rhs);
            final String comparison = ctx.COMPARISON().getText();

            if (lhsType == null || rhsType == null || rhsType != lhsType) {
                stack.push(comparison.equals("!=") || comparison.equals("<>"));
                return;
            }

            final Comparable lhValue = getValue(lhs);
            final Comparable rhValue = getValue(rhs);

            @SuppressWarnings("unchecked")
            final int value = lhValue.compareTo(rhValue);

            switch (comparison) {
                case ">":
                    stack.push(value > 0);
                    break;
                case "<":
                    stack.push(value < 0);
                    break;
                case "<=":
                    stack.push(value <= 0);
                    break;
                case ">=":
                    stack.push(value >= 0);
                    break;
                case "=":
                    stack.push(value == 0);
                    break;
                case "<>":
                case "!=":
                    stack.push(value != 0);
                    break;
                default:
                    stack.push(false);
                    break;
            }
        }
    }
}
