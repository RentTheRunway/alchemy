package io.rtr.alchemy.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import io.rtr.alchemy.filtering.FilterExpression;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import javax.validation.ValidationException;

public class TreatmentOverrideTest {
    final Treatment treatment = new Treatment("treatment");

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(TreatmentOverride.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .withPrefabValues(
                        FilterExpression.class,
                        FilterExpression.alwaysTrue(),
                        FilterExpression.of("false"))
                .verify();
    }

    @Test
    public void testValidName() throws ValidationException {
        String name = "a_valid_name";
        final TreatmentOverride override =
                new TreatmentOverride(name, mock(FilterExpression.class), treatment);
        assertEquals(override.getName(), name);
    }

    @Test(expected = ValidationException.class)
    public void testInvalidTreatmentName() throws ValidationException {
        final TreatmentOverride override =
                new TreatmentOverride(
                        "an invalid name with spaces", mock(FilterExpression.class), treatment);
        override.validateName();
    }
}
