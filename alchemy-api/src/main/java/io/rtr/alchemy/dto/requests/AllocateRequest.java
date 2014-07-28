package io.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Represents a simple allocation request
 */
public class AllocateRequest {
    @NotNull
    private final String treatment;
    @NotNull
    private final Integer size;

    public AllocateRequest(@JsonProperty("treatment") String treatment,
                           @JsonProperty("size") Integer size) {
        this.treatment = treatment;
        this.size = size;
    }

    public String getTreatment() {
        return treatment;
    }

    public Integer getSize() {
        return size;
    }
}
