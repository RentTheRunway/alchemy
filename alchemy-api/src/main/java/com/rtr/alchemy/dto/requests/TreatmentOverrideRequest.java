package com.rtr.alchemy.dto.requests;

import com.rtr.alchemy.dto.identities.IdentityDto;

import javax.validation.constraints.NotNull;

/**
 * Represents a request for creating an override
 */
public abstract class TreatmentOverrideRequest {
    @NotNull
    public abstract String getTreatment();

    @NotNull
    public abstract IdentityDto getIdentity();

    @NotNull
    public abstract String getName();
}
