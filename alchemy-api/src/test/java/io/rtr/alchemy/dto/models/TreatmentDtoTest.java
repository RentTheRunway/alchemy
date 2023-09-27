package io.rtr.alchemy.dto.models;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class TreatmentDtoTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(TreatmentDto.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }
}
