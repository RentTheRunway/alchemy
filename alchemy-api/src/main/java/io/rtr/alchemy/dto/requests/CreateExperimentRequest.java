package io.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.rtr.alchemy.dto.models.TreatmentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * Represents a request for creating an experiment
 */
public class CreateExperimentRequest {
    @NotNull
    private final String name;
    private final Integer seed;
    private final String description;
    private final String filter;
    private final Set<String> hashAttributes;
    private final Boolean active;
    @Valid
    private final List<TreatmentDto> treatments;
    @Valid
    private final List<AllocateRequest> allocations;
    @Valid
    private final List<TreatmentOverrideRequest> overrides;

    public CreateExperimentRequest(@JsonProperty("name") String name,
                                   @JsonProperty("seed") Integer seed,
                                   @JsonProperty("description") String description,
                                   @JsonProperty("filter") String filter,
                                   @JsonProperty("hashAttributes") Set<String> hashAttributes,
                                   @JsonProperty("active") Boolean active,
                                   @JsonProperty("treatments") List<TreatmentDto> treatments,
                                   @JsonProperty("allocations") List<AllocateRequest> allocations,
                                   @JsonProperty("overrides") List<TreatmentOverrideRequest> overrides) {
        this.name = name;
        this.seed = seed;
        this.description = description;
        this.filter = filter;
        this.hashAttributes = hashAttributes;
        this.active = active;
        this.treatments = treatments;
        this.allocations = allocations;
        this.overrides = overrides;
    }

    public String getName() {
        return name;
    }

    public Integer getSeed() {
        return seed;
    }

    public String getDescription() {
        return description;

    }
    public String getFilter() {
        return filter;
    }

    public Set<String> getHashAttributes() {
        return hashAttributes;
    }

    public Boolean isActive() {
        return active;
    }

    public List<TreatmentDto> getTreatments() {
        return treatments;
    }

    public List<AllocateRequest> getAllocations() {
        return allocations;
    }

    public List<TreatmentOverrideRequest> getOverrides() {
        return overrides;
    }
}
