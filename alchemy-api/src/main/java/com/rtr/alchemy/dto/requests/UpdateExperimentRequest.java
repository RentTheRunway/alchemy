package com.rtr.alchemy.dto.requests;

import com.google.common.base.Optional;
import com.rtr.alchemy.dto.models.TreatmentDto;

import javax.validation.Valid;
import java.util.List;

/**
 * Represents a request for updating an experiment
 */
public abstract class UpdateExperimentRequest {
    public abstract Optional<String> getDescription();
    public abstract Optional<String> getIdentityType();
    public abstract Optional<Boolean> getActive();

    @Valid
    public abstract Optional<List<TreatmentDto>> getTreatments();

    @Valid
    public abstract Optional<List<AllocateRequest>> getAllocations();

    @Valid
    public abstract Optional<List<TreatmentOverrideRequest>> getOverrides();
}
