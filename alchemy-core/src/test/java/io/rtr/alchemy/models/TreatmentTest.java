package io.rtr.alchemy.models;

import static org.junit.Assert.assertEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import javax.validation.ValidationException;

public class TreatmentTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(Treatment.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .withIgnoredFields("description")
                .verify();
    }

    @Test
    public void testValidTreatmentName() throws ValidationException {
        String name = "abc";
        final Treatment treatment = new Treatment(name, "a treatment with a valid name");
        assertEquals(treatment.getName(), name);
    }

    @Test(expected = ValidationException.class)
    public void testInvalidTreatmentName() throws ValidationException {
        final Treatment treatment = new Treatment(";;;", "a treatment with an invalid name");
        treatment.validateName();
    }
}
