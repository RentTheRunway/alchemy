package io.rtr.alchemy.models;

import com.google.common.base.Objects;

/**
 * Represents a possible user experience in an experiment
 */

public class Treatment {
    private final Integer name;
    private String description;

    public Treatment(Integer name) {
        this(name, null);
    }

    public Treatment(Integer name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
