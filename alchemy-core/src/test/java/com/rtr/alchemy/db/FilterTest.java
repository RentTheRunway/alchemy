package com.rtr.alchemy.db;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class FilterTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(Filter.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .verify();
    }
}
