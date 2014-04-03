package com.rtr.alchemy.models;

import com.google.common.base.Objects;

/**
 * Represents a possible user experience in an experiment
 */
public class Treatment {
    private final String name;
    private final String description;

    public Treatment(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Treatment)) {
            return false;
        }

        Treatment other = (Treatment) obj;
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
