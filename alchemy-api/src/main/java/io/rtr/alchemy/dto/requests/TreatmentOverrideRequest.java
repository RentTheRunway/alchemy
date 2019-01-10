package io.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Represents a request for creating an override
 */
public class TreatmentOverrideRequest {
    @NotNull
    private final Integer treatment;
    @NotNull
    private final String filter;
    @NotNull
    private final String name;

    public TreatmentOverrideRequest(@JsonProperty("treatment") Integer treatment,
                                    @JsonProperty("filter") String filter,
                                    @JsonProperty("name") String name) {
        this.treatment = treatment;
        this.filter = filter;
        this.name = name;
    }

    public Integer getTreatment() {
        return treatment;
    }

    public String getFilter() {
        return filter;
    }

    public String getName() {
        return name;
    }
}
