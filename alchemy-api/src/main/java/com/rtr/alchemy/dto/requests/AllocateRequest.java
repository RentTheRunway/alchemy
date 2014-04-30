package com.rtr.alchemy.dto.requests;

import javax.validation.constraints.NotNull;

/**
 * Represents a simple allocation request
 */
public abstract class AllocateRequest {
    @NotNull
    public abstract String getTreatment();

    @NotNull
    public abstract Integer getSize();
}
