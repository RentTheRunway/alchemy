package io.rtr.alchemy.filtering;

import io.rtr.alchemy.identities.AttributesMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterExpressionTest {
    private AttributesMap attributes;

    private boolean eval(String expression) {
        final FilterExpression filterExpression = FilterExpression.of(expression);
        return filterExpression.evaluate(attributes);
    }

    private void assertEval(String expression, boolean expected) {
        assertEquals(String.format("expected '%s' to be %s", expression, expected), expected, eval(expression));
    }

    private void assertComparison(String comparison, String leftValue, String rightValue,
                                  boolean expectedLeftRight, boolean expectedRightLeft, boolean expectedSame) {
        assertEval(String.format("%s%s%s", leftValue, comparison, rightValue), expectedLeftRight);
        assertEval(String.format("%s%s%s", rightValue, comparison, leftValue), expectedRightLeft);
        assertEval(String.format("%s%s%s", leftValue, comparison, leftValue), expectedSame);
        assertEval(String.format("%s%s%s", rightValue, comparison, rightValue), expectedSame);
    }

    @Before
    public void setUp() {
        attributes = AttributesMap
            .newBuilder()
            .put("foo", true)
            .put("not_foo", false)
            .put("one", 1)
            .put("zero", 0)
            .put("meaning_of_life", 42)
            .put("apple", "apple")
            .put("baz", "baz")
            .put("empty", "")
            .build();
    }

    @Test
    public void testSingularValueConstant() {
        // boolean
        assertEval("true", true);
        assertEval("false", false);

        // number
        assertEval("1", true);
        assertEval("42", true);
        assertEval("0", false);

        // string
        assertEval("\"\"", false);
        assertEval("\"foo\"", true);
        assertEval("\"false\"", true);
    }

    @Test
    public void testSingularValueIdentifier() {
        // boolean
        assertEval("foo", true);
        assertEval("not_foo", false);

        // number
        assertEval("one", true);
        assertEval("zero", false);
        assertEval("meaning_of_life", true);

        // string
        assertEval("baz", true);
        assertEval("empty", false);

        // does not exist
        assertEval("does_not_exist", false);
    }

    private void testComparisons(String booleanLhs, String booleanRhs,
                                 String numberLhs, String numberRhs,
                                 String stringLhs, String stringRhs) {
        // >
        assertComparison(">", booleanLhs, booleanRhs, true, false, false);
        assertComparison(">", numberLhs, numberRhs, true, false, false);
        assertComparison(">", stringLhs, stringRhs, true, false, false);

        // <
        assertComparison("<", booleanLhs, booleanRhs, false, true, false);
        assertComparison("<", numberLhs, numberRhs, false, true, false);
        assertComparison("<", stringLhs, stringRhs, false, true, false);

        // >=
        assertComparison(">=", booleanLhs, booleanRhs, true, false, true);
        assertComparison(">=", numberLhs, numberRhs, true, false, true);
        assertComparison(">=", stringLhs, stringRhs, true, false, true);

        // <=
        assertComparison("<=", booleanLhs, booleanRhs, false, true, true);
        assertComparison("<=", numberLhs, numberRhs, false, true, true);
        assertComparison("<=", stringLhs, stringRhs, false, true, true);

        // =
        assertComparison("=", booleanLhs, booleanRhs, false, false, true);
        assertComparison("=", numberLhs, numberRhs, false, false, true);
        assertComparison("=", stringLhs, stringRhs, false, false, true);

        // !=
        assertComparison("!=", booleanLhs, booleanRhs, true, true, false);
        assertComparison("!=", numberLhs, numberRhs, true, true, false);
        assertComparison("!=", stringLhs, stringRhs, true, true, false);

        // <>
        assertComparison("<>", booleanLhs, booleanRhs, true, true, false);
        assertComparison("<>", numberLhs, numberRhs, true, true, false);
        assertComparison("<>", stringLhs, stringRhs, true, true, false);
    }

    @Test
    public void testConstantAndConstantComparison() {
        testComparisons(
                "true", "false",
                "1", "0",
                "\"bear\"", "\"apple\""
        );
    }

    @Test
    public void testConstantAndIdentifierComparison() {
        testComparisons(
                "true", "not_foo",
                "1", "zero",
                "baz", "\"apple\""
        );
    }

    @Test
    public void testIdentifierAndIdentifierComparison() {
        testComparisons(
                "foo", "not_foo",
                "one", "zero",
                "baz", "apple"
        );
    }

    @Test
    public void testComplexComparisons() {
        assertEval("foo & !not_foo", true);
        assertEval("foo | not_foo", true);
        assertEval("one > 0 & meaning_of_life = 42", true);
        assertEval("(one > 0 | !foo) & (meaning_of_life = 42 | empty)", true);
        assertEval("!!!not_foo > false", true);
        assertEval("!foo | not_foo", false);
        assertEval("foo & not_foo", false);
    }

    @Test
    public void testComparisonsTypeMismatch() {
        assertEval("1 > true", false);
        assertEval("1 < true", false);
        assertEval("1 = true", false);
        assertEval("1 >= true", false);
        assertEval("1 <= true", false);
        assertEval("1 != true", true);
    }

    @Test
    public void testIsValid() {
        assertTrue(FilterExpression.isValid("a"));
        assertTrue(FilterExpression.isValid("a|b"));
        assertTrue(FilterExpression.isValid("!a"));
        assertTrue(FilterExpression.isValid("(((a)))"));
        assertFalse(FilterExpression.isValid("|"));
        assertFalse(FilterExpression.isValid("a|"));
        assertFalse(FilterExpression.isValid("|b"));
        assertFalse(FilterExpression.isValid("!|"));
        assertFalse(FilterExpression.isValid("!|b"));
        assertFalse(FilterExpression.isValid("((a)"));
    }
}
