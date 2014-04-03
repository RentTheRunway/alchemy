package com.rtr.alchemy.models;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class TreatmentOverrideTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(TreatmentOverride.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .verify();
    }
}
