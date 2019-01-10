package io.rtr.alchemy.dto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Represents a treatment override
 */
public class TreatmentOverrideDto {
    private final String name;
    private final String filter;
    private final Integer treatment;

    public TreatmentOverrideDto(@JsonProperty("name") String name,
                                @JsonProperty("filter") String filter,
                                @JsonProperty("treatment") Integer treatment) {
        this.name = name;
        this.filter = filter;
        this.treatment = treatment;
    }

    public String getName() {
        return name;
    }

    public String getFilter() {
        return filter;
    }

    public Integer getTreatment() {
        return treatment;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, filter, treatment);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TreatmentOverrideDto)) {
            return false;
        }

        final TreatmentOverrideDto other = (TreatmentOverrideDto) obj;
        return
            Objects.equal(name, other.name) &&
            Objects.equal(filter, other.filter) &&
            Objects.equal(treatment, other.treatment);
    }
}
