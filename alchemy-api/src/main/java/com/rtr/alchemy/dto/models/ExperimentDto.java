package com.rtr.alchemy.dto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

/**
 * Represents an experiment
 */
public class ExperimentDto {
    private final String name;
    private final int seed;
    private final String description;
    private final Set<String> segments;
    private final boolean active;
    private final DateTime created;
    private final DateTime modified;
    private final DateTime activated;
    private final DateTime deactivated;
    private final List<TreatmentDto> treatments;
    private final List<AllocationDto> allocations;
    private final List<TreatmentOverrideDto> overrides;

    @JsonCreator
    public ExperimentDto(@JsonProperty("name") String name,
                         @JsonProperty("seed") int seed,
                         @JsonProperty("description") String description,
                         @JsonProperty("segments") Set<String> segments,
                         @JsonProperty("active") boolean active,
                         @JsonProperty("created") DateTime created,
                         @JsonProperty("modified") DateTime modified,
                         @JsonProperty("activated") DateTime activated,
                         @JsonProperty("deactivated") DateTime deactivated,
                         @JsonProperty("treatments") List<TreatmentDto> treatments,
                         @JsonProperty("allocations") List<AllocationDto> allocations,
                         @JsonProperty("overrides") List<TreatmentOverrideDto> overrides) {
        this.name = name;
        this.seed = seed;
        this.description = description;
        this.segments = segments;
        this.active = active;
        this.created = created;
        this.modified = modified;
        this.activated = activated;
        this.deactivated = deactivated;
        this.treatments = treatments;
        this.allocations = allocations;
        this.overrides = overrides;
    }

    public String getName() {
        return name;
    }

    public int getSeed() {
        return seed;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getSegments() {
        return segments;
    }

    public boolean isActive() {
        return active;
    }

    public DateTime getCreated() {
        return created;
    }

    public DateTime getModified() {
        return modified;
    }

    public DateTime getActivated() {
        return activated;
    }

    public DateTime getDeactivated() {
        return deactivated;
    }

    public List<TreatmentDto> getTreatments() {
        return treatments;
    }

    public List<AllocationDto> getAllocations() {
        return allocations;
    }

    public List<TreatmentOverrideDto> getOverrides() {
        return overrides;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExperimentDto)) {
            return false;
        }

        final ExperimentDto other = (ExperimentDto) obj;

        return Objects.equal(name, other.name);
    }
}
