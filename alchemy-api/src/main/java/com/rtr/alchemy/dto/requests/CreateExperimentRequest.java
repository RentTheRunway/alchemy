package com.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.alchemy.dto.models.TreatmentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * Represents a request for creating an experiment
 */
public class CreateExperimentRequest {
    @NotNull
    private final String name;
    private final Integer seed;
    private final String description;
    private final Set<String> segments;
    private final Boolean active;
    @Valid
    private final List<TreatmentDto> treatments;
    @Valid
    private final List<AllocateRequest> allocations;
    @Valid
    private final List<TreatmentOverrideRequest> overrides;

    public CreateExperimentRequest(@JsonProperty("name") String name,
                                   @JsonProperty("seed") Integer seed,
                                   @JsonProperty("description") String description,
                                   @JsonProperty("segments") Set<String> segments,
                                   @JsonProperty("active") Boolean active,
                                   @JsonProperty("treatments") List<TreatmentDto> treatments,
                                   @JsonProperty("allocations") List<AllocateRequest> allocations,
                                   @JsonProperty("overrides") List<TreatmentOverrideRequest> overrides) {
        this.name = name;
        this.seed = seed;
        this.description = description;
        this.segments = segments;
        this.active = active;
        this.treatments = treatments;
        this.allocations = allocations;
        this.overrides = overrides;
    }

    public String getName() {
        return name;
    }

    public Integer getSeed() {
        return seed;
    }

    public String getDescription() {
        return description;

    }
    public Set<String> getSegments() {
        return segments;
    }

    public Boolean isActive() {
        return active;
    }

    public List<TreatmentDto> getTreatments() {
        return treatments;
    }

    public List<AllocateRequest> getAllocations() {
        return allocations;
    }

    public List<TreatmentOverrideRequest> getOverrides() {
        return overrides;
    }
}
