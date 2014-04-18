package com.rtr.alchemy.dto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;

/**
 * Represents a treatment
 */
public class TreatmentDto {
    @NotNull
    private final String name;
    private final String description;

    @JsonCreator
    public TreatmentDto(@JsonProperty("name") String name,
                        @JsonProperty("description") String description) {
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
        if (!(obj instanceof TreatmentDto)) {
            return false;
        }

        final TreatmentDto other = (TreatmentDto) obj;

        return
            Objects.equal(name, other.name);
    }
}
