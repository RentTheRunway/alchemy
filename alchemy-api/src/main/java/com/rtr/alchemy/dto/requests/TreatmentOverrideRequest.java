package com.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.alchemy.dto.identities.IdentityDto;

import javax.validation.constraints.NotNull;

/**
 * Represents a request for creating an override
 */
public class TreatmentOverrideRequest {
    @NotNull
    private final String treatment;
    @NotNull
    private final IdentityDto identity;
    @NotNull
    private final String name;

    public TreatmentOverrideRequest(@JsonProperty("treatment") String treatment,
                                    @JsonProperty("identity") IdentityDto identity,
                                    @JsonProperty("name") String name) {
        this.treatment = treatment;
        this.identity = identity;
        this.name = name;
    }

    public String getTreatment() {
        return treatment;
    }

    public IdentityDto getIdentity() {
        return identity;
    }

    public String getName() {
        return name;
    }
}
