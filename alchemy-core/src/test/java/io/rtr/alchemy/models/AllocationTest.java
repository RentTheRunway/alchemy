package io.rtr.alchemy.models;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class AllocationTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(Allocation.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }
}
