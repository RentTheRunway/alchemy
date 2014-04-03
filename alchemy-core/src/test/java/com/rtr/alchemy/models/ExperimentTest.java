package com.rtr.alchemy.models;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class ExperimentTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(Experiment.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .verify();
    }
}
