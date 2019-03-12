package io.rtr.alchemy.models;

import org.junit.Test;

import javax.validation.ValidationException;

public class NamedTest {
    private static void assertValid(String name) {
        ((Named) () -> name).validateName();
    }

    private static void assertNotValid(String name) {
        try {
            ((Named) () -> name).validateName();
            throw new AssertionError("expected validateName() to throw");
        } catch (ValidationException ignored) {
        }
    }

    @Test
    public void testValidation() {
        assertNotValid(null);
        assertNotValid("");
        assertNotValid("!");
        assertValid("mIxEd-CaSe");
        assertValid("snake_case");
        assertValid("l33t-sp34k");
    }
}
