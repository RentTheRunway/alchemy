package io.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/** Represents a request for creating an override */
public class TreatmentOverrideRequest {
    @NotNull private final String treatment;
    @NotNull private final String filter;
    @NotNull private final String name;

    public TreatmentOverrideRequest(
            @JsonProperty("treatment") String treatment,
            @JsonProperty("filter") String filter,
            @JsonProperty("name") String name) {
        this.treatment = treatment;
        this.filter = filter;
        this.name = name;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getFilter() {
        return filter;
    }

    public String getName() {
        return name;
    }
}
