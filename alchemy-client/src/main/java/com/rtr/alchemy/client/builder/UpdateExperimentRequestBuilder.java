package com.rtr.alchemy.client.builder;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.requests.AllocateRequest;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import com.rtr.alchemy.dto.requests.UpdateExperimentRequest;
import com.sun.jersey.api.client.WebResource;

import java.util.List;
import java.util.Set;

public class UpdateExperimentRequestBuilder {
    private final WebResource.Builder builder;
    private Optional<Integer> seed;
    private Optional<String> description;
    private Optional<String> filter;
    private Optional<Set<String>> hashAttributes;
    private Optional<Boolean> active;
    private Optional<List<TreatmentDto>> treatments;
    private Optional<List<AllocateRequest>> allocations;
    private Optional<List<TreatmentOverrideRequest>> overrides;

    public UpdateExperimentRequestBuilder(WebResource.Builder builder) {
        this.builder = builder;
    }

    public UpdateExperimentRequestBuilder setSeed(int seed) {
        this.seed = Optional.of(seed);
        return this;
    }

    public UpdateExperimentRequestBuilder setDescription(String description) {
        this.description = Optional.fromNullable(description);
        return this;
    }

    public UpdateExperimentRequestBuilder setFilter(String filter) {
        this.filter = Optional.fromNullable(filter);
        return this;
    }

    public UpdateExperimentRequestBuilder setHashAttributes(Set<String> hashAttributes) {
        this.hashAttributes = Optional.<Set<String>>fromNullable(Sets.newLinkedHashSet(hashAttributes));
        return this;
    }

    public UpdateExperimentRequestBuilder setHashAttributes(String ... hashAttributes) {
        this.hashAttributes = Optional.<Set<String>>fromNullable(Sets.newLinkedHashSet(Lists.newArrayList(hashAttributes)));
        return this;
    }

    public UpdateExperimentRequestBuilder activate() {
        active = Optional.of(true);
        return this;
    }

    public UpdateExperimentRequestBuilder deactivate() {
        active = Optional.of(false);
        return this;
    }

    public UpdateExperimentRequestBuilder setTreatments(List<TreatmentDto> treatments) {
        this.treatments = Optional.fromNullable(treatments);
        return this;
    }

    public UpdateExperimentRequestBuilder setAllocations(List<AllocateRequest> allocations) {
        this.allocations = Optional.fromNullable(allocations);
        return this;
    }

    public UpdateExperimentRequestBuilder setOverrides(List<TreatmentOverrideRequest> overrides) {
        this.overrides = Optional.fromNullable(overrides);
        return this;
    }

    public void apply() {
        builder.post(
            new UpdateExperimentRequest(
                seed,
                description,
                filter,
                hashAttributes,
                active,
                treatments,
                allocations,
                overrides
            )
        );
    }

}
