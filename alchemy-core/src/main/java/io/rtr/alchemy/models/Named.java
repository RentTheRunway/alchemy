package io.rtr.alchemy.models;

import javax.validation.ValidationException;
import java.util.regex.Pattern;

public interface Named {
    Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9-_]*$");

    String getName();

    default void validateName() {
        if (!NAME_PATTERN.matcher(getName()).matches()) {
            throw new ValidationException(
                String.format(
                    "Invalid name %s, must match %s",
                    getName(),
                    NAME_PATTERN.pattern()
                )
            );
        }
    }
}
