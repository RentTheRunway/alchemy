package com.rtr.alchemy.dto.requests;

import com.rtr.alchemy.dto.models.TreatmentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents a request for creating an experiment
 */
public abstract class CreateExperimentRequest {
    @NotNull
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getIdentityType();
    public abstract Boolean isActive();

    @Valid
    public abstract List<TreatmentDto> getTreatments();

    @Valid
    public abstract List<AllocateRequest> getAllocations();

    @Valid
    public abstract List<TreatmentOverrideRequest> getOverrides();
}
