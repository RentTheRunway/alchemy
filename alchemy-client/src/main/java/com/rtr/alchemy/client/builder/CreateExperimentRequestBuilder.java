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

    public CreateExperimentRequestBuilder allocate(final String treatmentName, final int size) {
        allocations.add(new AllocateRequest() {
            @Override
            public String getTreatment() {
                return treatmentName;
            }

            @Override
            public Integer getSize() {
                return size;
            }
        });

        return this;
    }

    public CreateExperimentRequestBuilder addOverride(final String name, final String treatmentName, final IdentityDto identity) {
        overrides.add(new TreatmentOverrideRequest() {
            @Override
            public String getTreatment() {
                return treatmentName;
            }

            @Override
            public IdentityDto getIdentity() {
                return identity;
            }

            @Override
            public String getName() {
                return name;
            }
        });

        return this;
    }

    public void apply() {
        builder.put(new CreateExperimentRequest() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getIdentityType() {
                return identityType;
            }

            @Override
            public Boolean isActive() {
                return isActive;
            }

            @Override
            public List<TreatmentDto> getTreatments() {
                return treatments;
            }

            @Override
            public List<AllocateRequest> getAllocations() {
                return allocations;
            }

            @Override
            public List<TreatmentOverrideRequest> getOverrides() {
                return overrides;
            }
        });
    }
}
