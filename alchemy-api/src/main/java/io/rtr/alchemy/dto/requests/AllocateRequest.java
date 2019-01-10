package io.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Represents a simple allocation request
 */
public class AllocateRequest {
    @NotNull
    private final Integer treatment;
    @NotNull
    private final Integer size;

    public AllocateRequest(@JsonProperty("treatment") Integer treatment,
                           @JsonProperty("size") Integer size) {
        this.treatment = treatment;
        this.size = size;
    }

    public Integer getTreatment() {
        return treatment;
    }

    public Integer getSize() {
        return size;
    }
}
