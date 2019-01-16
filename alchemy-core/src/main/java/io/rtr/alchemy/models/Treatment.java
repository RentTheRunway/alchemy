package io.rtr.alchemy.models;

import com.google.common.base.Objects;
import io.rtr.alchemy.models.NameException;

/**
 * Represents a possible user experience in an experiment
 */



public class Treatment {
    private final String name;
    private String description;

    public Treatment(String name) {
        this(name, null);
    }

    public Treatment(String name, String description) {
        this.validateName(name);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private static void validateName(String name) {
        // see https://stackoverflow.com/questions/1969232/allowed-characters-in-cookies/45536811#45536811
        if (! name.matches("^[A-Za-z0-9!$&'()*+-.:@_~]*$")) {
            throw new NameException("Invalid name, must match ^[A-Za-z0-9!$&'()*+-.:@_~]*$");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Treatment)) {
            return false;
        }

        final Treatment other = (Treatment) obj;
        return
            Objects.equal(name, other.name);
    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("name", name)
                .add("description", description)
                .toString();
    }
}
