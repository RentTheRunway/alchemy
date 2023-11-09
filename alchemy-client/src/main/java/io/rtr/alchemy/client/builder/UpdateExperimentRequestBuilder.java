package io.rtr.alchemy.client.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.dto.requests.AllocateRequest;
import io.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import io.rtr.alchemy.dto.requests.UpdateExperimentRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

public class UpdateExperimentRequestBuilder {
    private final Invocation.Builder builder;
    private Optional<Integer> seed;
    private Optional<String> description;
    private Optional<String> filter;
    private Optional<Set<String>> hashAttributes;
    private Optional<Boolean> active;
    private Optional<List<TreatmentDto>> treatments;
    private Optional<List<AllocateRequest>> allocations;
    private Optional<List<TreatmentOverrideRequest>> overrides;

    public UpdateExperimentRequestBuilder(Invocation.Builder builder) {
        this.builder = builder;
    }

    public UpdateExperimentRequestBuilder setSeed(int seed) {
        this.seed = Optional.of(seed);
        return this;
    }

    public UpdateExperimentRequestBuilder setDescription(String description) {
        this.description = Optional.ofNullable(description);
        return this;
    }

    public UpdateExperimentRequestBuilder setFilter(String filter) {
        this.filter = Optional.ofNullable(filter);
        return this;
    }

    public UpdateExperimentRequestBuilder setHashAttributes(Set<String> hashAttributes) {
        this.hashAttributes = Optional.ofNullable(Sets.newLinkedHashSet(hashAttributes));
        return this;
    }

    public UpdateExperimentRequestBuilder setHashAttributes(String... hashAttributes) {
        this.hashAttributes =
                Optional.ofNullable(Sets.newLinkedHashSet(Lists.newArrayList(hashAttributes)));
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
        this.treatments = Optional.ofNullable(treatments);
        return this;
    }

    public UpdateExperimentRequestBuilder setAllocations(List<AllocateRequest> allocations) {
        this.allocations = Optional.ofNullable(allocations);
        return this;
    }

    public UpdateExperimentRequestBuilder setOverrides(List<TreatmentOverrideRequest> overrides) {
        this.overrides = Optional.ofNullable(overrides);
        return this;
    }

    public void apply() {
        builder.post(
                Entity.entity(
                        new UpdateExperimentRequest(
                                seed,
                                description,
                                filter,
                                hashAttributes,
                                active,
                                treatments,
                                allocations,
                                overrides),
                        MediaType.APPLICATION_JSON_TYPE));
    }
}
