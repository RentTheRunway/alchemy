package com.rtr.alchemy.filtering;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterParserTest {

    private boolean eval(Set<String> attribtes, String expression) {
        final FilterExpression filterExpression = new FilterExpression(expression);
        return filterExpression.evaluate(attribtes);
    }

    @Test
    public void testExpressions() {
        final Set<String> attributes = Sets.newHashSet("a", "e", "i", "o", "u");

        // short form
        assertTrue(eval(attributes, "a"));
        assertFalse(eval(attributes, "b"));
        assertFalse(eval(attributes, "!a"));
        assertTrue(eval(attributes, "!b"));
        assertTrue(eval(attributes, "!!a"));
        assertFalse(eval(attributes, "!!b"));
        assertTrue(eval(attributes, "!(!a)"));
        assertTrue(eval(attributes, "a|b"));
        assertTrue(eval(attributes, "a|!b"));
        assertFalse(eval(attributes, "!a|b"));
        assertFalse(eval(attributes, "(a|b)&(b|c)&(!a|!b)&(!b|!c)"));
        assertTrue(eval(attributes, "(a|b)&(!a|!b)&(!b|!c)"));
        assertTrue(eval(attributes, "(((a|b)|c)|d)"));

        // long form
        assertTrue(eval(attributes, "a"));
        assertFalse(eval(attributes, "b"));
        assertFalse(eval(attributes, "not a"));
        assertTrue(eval(attributes, "not b"));
        assertTrue(eval(attributes, "not not a"));
        assertFalse(eval(attributes, "not not b"));
        assertTrue(eval(attributes, "not(not a)"));
        assertTrue(eval(attributes, "a or b"));
        assertTrue(eval(attributes, "a or not b"));
        assertFalse(eval(attributes, "not a or b"));

        // mix
        assertTrue(eval(attributes, "(not ! not ! a | not not not ! b) and not !a"));
    }
}
