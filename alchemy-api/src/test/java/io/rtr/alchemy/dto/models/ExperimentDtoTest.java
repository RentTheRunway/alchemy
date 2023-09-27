package io.rtr.alchemy.dto.models;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class ExperimentDtoTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(ExperimentDto.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }
}
