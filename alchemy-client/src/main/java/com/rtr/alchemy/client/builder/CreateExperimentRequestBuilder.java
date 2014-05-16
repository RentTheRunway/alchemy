package com.rtr.alchemy.client.builder;

import com.google.common.collect.Lists;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.requests.AllocateRequest;
import com.rtr.alchemy.dto.requests.CreateExperimentRequest;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

public class CreateExperimentRequestBuilder {
    private final String name;
    private final WebResource.Builder builder;
    private String description;
    private String identityType;
    private Boolean isActive;
    private final List<TreatmentDto> treatments;
    private final List<AllocateRequest> allocations;
    private final List<TreatmentOverrideRequest> overrides;

    public CreateExperimentRequestBuilder(String name, WebResource.Builder builder) {
        this.name = name;
        this.builder = builder;
        this.treatments = Lists.newArrayList();
        this.allocations = Lists.newArrayList();
        this.overrides = Lists.newArrayList();
    }

    public CreateExperimentRequestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CreateExperimentRequestBuilder setIdentityType(String identityType) {
        this.identityType = identityType;
        return this;
    }

    public CreateExperimentRequestBuilder activate() {
        isActive = true;
        return this;
    }

    public CreateExperimentRequestBuilder deactivate() {
        isActive = false;
        return this;
    }

    public CreateExperimentRequestBuilder addTreatment(String name) {
        treatments.add(new TreatmentDto(name, null));
        return this;
    }

    public CreateExperimentRequestBuilder addTreatment(String name, String description) {
        treatments.add(new TreatmentDto(name, description));
        return this;
    }

    public CreateExperimentRequestBuilder allocate(String treatmentName, int size) {
        allocations.add(new AllocateRequest(treatmentName, size));
        return this;
    }

    public CreateExperimentRequestBuilder addOverride(String name, String treatmentName, IdentityDto identity) {
        overrides.add(new TreatmentOverrideRequest(treatmentName, identity, name));
        return this;
    }

    public void apply() {
        builder.put(
            new CreateExperimentRequest(
                name,
                description,
                identityType,
                isActive,
                treatments,
                allocations,
                overrides)
        );
    }
}
