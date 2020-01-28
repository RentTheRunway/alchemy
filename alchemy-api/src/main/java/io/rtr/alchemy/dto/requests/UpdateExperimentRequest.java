package io.rtr.alchemy.dto.requests;

import io.rtr.alchemy.dto.models.TreatmentDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a request for updating an experiment
 */
public class UpdateExperimentRequest {
    private Optional<Integer> seed;
    private Optional<String> description;
    private Optional<String> filter;
    private Optional<Set<String>> hashAttributes;
    private Optional<Boolean> active;

    @Valid
    private Optional<List<TreatmentDto>> treatments;

    @Valid
    private Optional<List<AllocateRequest>> allocations;

    @Valid
    private Optional<List<TreatmentOverrideRequest>> overrides;

    public UpdateExperimentRequest() { }

    public UpdateExperimentRequest(Optional<Integer> seed,
                                   Optional<String> description,
                                   Optional<String> filter,
                                   Optional<Set<String>> hashAttributes,
                                   Optional<Boolean> active,
                                   Optional<List<TreatmentDto>> treatments,
                                   Optional<List<AllocateRequest>> allocations,
                                   Optional<List<TreatmentOverrideRequest>> overrides) {
        this.seed = seed;
        this.description = description;
        this.filter = filter;
        this.hashAttributes = hashAttributes;
        this.active = active;
        this.treatments = treatments;
        this.allocations = allocations;
        this.overrides = overrides;
    }
    public Optional<Integer> getSeed() {
        return seed;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<String> getFilter() {
        return filter;
    }

    public Optional<Set<String>> getHashAttributes() {
        return hashAttributes;
    }

    public Optional<Boolean> getActive() {
        return active;
    }

    public Optional<List<TreatmentDto>> getTreatments() {
        return treatments;
    }

    public Optional<List<AllocateRequest>> getAllocations() {
        return allocations;
    }

    public Optional<List<TreatmentOverrideRequest>> getOverrides() {
        return overrides;
    }

    // NOTE: Need setters in order for Optional<T> to work correctly
    public void setSeed(Optional<Integer> seed) {
        this.seed = seed;
    }

    public void setDescription(Optional<String> description) {
        this.description = description;
    }

    public void setFilter(Optional<String> filter) {
        this.filter = filter;
    }

    public void setHashAttributes(Optional<Set<String>> hashAttributes) {
        this.hashAttributes = hashAttributes;
    }

    public void setActive(Optional<Boolean> active) {
        this.active = active;
    }

    public void setTreatments(Optional<List<TreatmentDto>> treatments) {
        this.treatments = treatments;
    }

    public void setAllocations(Optional<List<AllocateRequest>> allocations) {
        this.allocations = allocations;
    }

    public void setOverrides(Optional<List<TreatmentOverrideRequest>> overrides) {
        this.overrides = overrides;
    }
}
