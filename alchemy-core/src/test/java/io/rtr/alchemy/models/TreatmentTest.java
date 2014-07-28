package io.rtr.alchemy.models;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class TreatmentTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(Treatment.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .verify();
    }
}
