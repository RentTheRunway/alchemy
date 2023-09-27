package io.rtr.alchemy.client.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.dto.requests.AllocateRequest;
import io.rtr.alchemy.dto.requests.CreateExperimentRequest;
import io.rtr.alchemy.dto.requests.TreatmentOverrideRequest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class CreateExperimentRequestBuilder {
    private final String name;
    private final Invocation.Builder builder;
    private Integer seed;
    private String description;
    private String filter;
    private Set<String> hashAttributes;
    private Boolean isActive;
    private final List<TreatmentDto> treatments;
    private final List<AllocateRequest> allocations;
    private final List<TreatmentOverrideRequest> overrides;

    public CreateExperimentRequestBuilder(String name, Invocation.Builder builder) {
        this.name = name;
        this.builder = builder;
        this.treatments = Lists.newArrayList();
        this.allocations = Lists.newArrayList();
        this.overrides = Lists.newArrayList();
    }

    public CreateExperimentRequestBuilder setSeed(int seed) {
        this.seed = seed;
        return this;
    }

    public CreateExperimentRequestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CreateExperimentRequestBuilder setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public CreateExperimentRequestBuilder setHashAttributes(Set<String> hashAttributes) {
        this.hashAttributes = Sets.newLinkedHashSet(hashAttributes);
        return this;
    }

    public CreateExperimentRequestBuilder setHashAttributes(String... hashAttributes) {
        this.hashAttributes = Sets.newLinkedHashSet(Lists.newArrayList(hashAttributes));
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

    public CreateExperimentRequestBuilder addOverride(
            String name, String treatmentName, String filter) {
        overrides.add(new TreatmentOverrideRequest(treatmentName, filter, name));
        return this;
    }

    public void apply() {
        builder.put(
                Entity.entity(
                        new CreateExperimentRequest(
                                name,
                                seed,
                                description,
                                filter,
                                hashAttributes,
                                isActive,
                                treatments,
                                allocations,
                                overrides),
                        APPLICATION_JSON));
    }
}
