package com.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.alchemy.dto.models.TreatmentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents a request for creating an experiment
 */
public class CreateExperimentRequest {
    @NotNull
    private final String name;
    private final String description;
    private final String identityType;
    private final Boolean active;
    @Valid
    private final List<TreatmentDto> treatments;
    @Valid
    private final List<AllocateRequest> allocations;
    @Valid
    private final List<TreatmentOverrideRequest> overrides;

    public CreateExperimentRequest(@JsonProperty("name") String name,
                                   @JsonProperty("description") String description,
                                   @JsonProperty("identityType") String identityType,
                                   @JsonProperty("active") Boolean active,
                                   @JsonProperty("treatments") List<TreatmentDto> treatments,
                                   @JsonProperty("allocations") List<AllocateRequest> allocations,
                                   @JsonProperty("overrides") List<TreatmentOverrideRequest> overrides) {
        this.name = name;
        this.description = description;
        this.identityType = identityType;
        this.active = active;
        this.treatments = treatments;
        this.allocations = allocations;
        this.overrides = overrides;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;

    }
    public String getIdentityType() {
        return identityType;
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
