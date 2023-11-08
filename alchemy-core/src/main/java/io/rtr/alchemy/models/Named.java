package io.rtr.alchemy.models;

import java.util.regex.Pattern;

import javax.validation.ValidationException;

public interface Named {
    Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+$");

    String getName();

    default void validateName() {
        if (getName() == null || !NAME_PATTERN.matcher(getName()).matches()) {
            throw new ValidationException(
                    String.format(
                            "Invalid name %s, must match %s", getName(), NAME_PATTERN.pattern()));
        }
    }
}
