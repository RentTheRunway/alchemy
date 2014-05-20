package com.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.rtr.alchemy.dto.models.TreatmentDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Represents a request for updating an experiment
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateExperimentRequest {
    private final Optional<String> description;
    private final Optional<Set<String>> segments;
    private final Optional<Boolean> active;
    @Valid
    private final Optional<List<TreatmentDto>> treatments;
    @Valid
    private final Optional<List<AllocateRequest>> allocations;
    @Valid
    private final Optional<List<TreatmentOverrideRequest>> overrides;

    public UpdateExperimentRequest(@JsonProperty("description") Optional<String> description,
                                   @JsonProperty("segments") Optional<Set<String>> segments,
                                   @JsonProperty("active") Optional<Boolean> active,
                                   @JsonProperty("treatments") Optional<List<TreatmentDto>> treatments,
                                   @JsonProperty("allocations") Optional<List<AllocateRequest>> allocations,
                                   @JsonProperty("overrides") Optional<List<TreatmentOverrideRequest>> overrides) {
        this.description = description;
        this.segments = segments;
        this.active = active;
        this.treatments = treatments;
        this.allocations = allocations;
        this.overrides = overrides;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<Set<String>> getSegments() {
        return segments;
    }

    public Optional<Boolean> getActive() {
        return active;
    }

    public Optional<List<TreatmentDto>> getTreatments() {
        return treatments;
    }

    public Optional<List<AllocateRequest>> getAllocations() {
        return allocations;
    }

    public Optional<List<TreatmentOverrideRequest>> getOverrides() {
        return overrides;
    }
}
