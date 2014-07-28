package io.rtr.alchemy.models;

import io.rtr.alchemy.filtering.FilterExpression;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class TreatmentOverrideTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(TreatmentOverride.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withPrefabValues(FilterExpression.class, mock(FilterExpression.class), mock(FilterExpression.class))
            .verify();
    }
}
