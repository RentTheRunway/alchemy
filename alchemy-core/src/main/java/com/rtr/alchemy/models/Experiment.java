package com.rtr.alchemy.models;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.IdentityBuilder;
import org.apache.commons.math3.util.FastMath;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;
import java.util.Map;

/**
 * Represents a collection of user experiences being tested
 */
public class Experiment {
    private final ExperimentsStore store;
    private final String name;
    private final Allocations allocations;
    private final Map<String, Treatment> treatments;
    private final Map<Long, TreatmentOverride> overrides;
    private final int seed;
    private String description;
    private String identityType;
    private boolean active;
    private DateTime created;
    private DateTime modified;
    private DateTime activated;
    private DateTime deactivated;


    // used by Builder when loading experiment from store
    private Experiment(ExperimentsStore store,
                       String name,
                       String description,
                       String identityType,
                       boolean active,
                       DateTime created,
                       DateTime modified,
                       DateTime activated,
                       DateTime deactivated,
                       Map<String, Treatment> treatments,
                       Iterable<TreatmentOverride> overrides,
                       Iterable<Allocation> allocations) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.identityType = identityType;
        this.active = active;
        this.created = created;
        this.modified = modified;
        this.activated = activated;
        this.deactivated = deactivated;

        this.treatments = treatments;
        for (Treatment treatment : treatments.values()) {
            this.treatments.put(treatment.getName(), treatment);
        }

        this.overrides = Maps.newConcurrentMap();
        for (TreatmentOverride override : overrides) {
            this.overrides.put(override.getHash(), override);
        }

        this.allocations = new Allocations(allocations);
        this.seed = (int) IdentityBuilder.seed(0).putString(name).hash();
    }

    // used when creating a new experiment
    protected Experiment(ExperimentsStore store,
                         String name) {
        this.store = store;
        this.name = name;
        this.allocations = new Allocations();
        this.treatments = Maps.newConcurrentMap();
        this.overrides = Maps.newConcurrentMap();
        this.seed = (int) IdentityBuilder.seed(0).putString(name).hash();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Experiment setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getIdentityType() {
        return identityType;
    }

    public Experiment setIdentityType(String identityType) {
        this.identityType = identityType;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public DateTime getCreated() {
        return created;
    }

    public DateTime getModified() {
        return modified;
    }

    protected void setModified(DateTime modified) {
        this.modified = modified;
    }

    public DateTime getActivated() {
        return activated;
    }

    public DateTime getDeactivated() {
        return deactivated;
    }

    /**
     * Gets all allocations defined on this experiment
     */
    public Iterable<Allocation> getAllocations() {
        return ImmutableList.copyOf(allocations.getAllocations());
    }

    /**
     * Gets all treatments defined on this experiment
     */
    public Iterable<Treatment> getTreatments() {
        return ImmutableList.copyOf(treatments.values());
    }

    /**
     * Gets all overrides defined on this experiment
     */
    public Iterable<TreatmentOverride> getOverrides() {
        return ImmutableList.copyOf(overrides.values());
    }

    /**
     * Gets the assigned override for a given identity
     * @param identity The identity
     */
    public TreatmentOverride getOverride(Identity identity) {
        return overrides.get(identity.getHash(seed));
    }

    /**
     * Activates the experiments, enabling all treatments
     */
    public Experiment activate() {
        if (active) {
            return this;
        }

        active = true;
        activated = DateTime.now(DateTimeZone.UTC);
        return this;
    }

    /**
     * Deactivates the experiment, disabling all treatments
     */
    public Experiment deactivate() {
        if (!active) {
            return this;
        }

        active = false;
        deactivated = DateTime.now(DateTimeZone.UTC);
        return this;
    }

    /**
     * Adds a treatment
     * @param name The name
     */
    public Experiment addTreatment(String name) {
        treatments.put(name, new Treatment(name));
        return this;
    }

    /**
     * Adds a treatment
     * @param name The name
     * @param description The description
     */
    public Experiment addTreatment(String name, String description) {
        treatments.put(name, new Treatment(name, description));
        return this;
    }

    /**
     * Add a treatment override for an identity
     * @param treatmentName The treatment an identity should receive
     * @param identity The identity
     */
    public Experiment addOverride(String treatmentName, Identity identity) {
        final Long hash = identity.getHash(seed);
        overrides.put(hash, new TreatmentOverride(identity.toString(), hash, treatment(treatmentName)));
        return this;
    }

    /**
     * Remove an override
     * @param identity The identity to remove the override for
     */
    public Experiment removeOverride(Identity identity) {
        final Long hash = identity.getHash(seed);
        overrides.remove(hash);
        return this;
    }

    /**
     * Removes a treatment
     * @param name The treatment
     */
    public Experiment removeTreatment(String name) {
        treatments.remove(name);
        return this;
    }

    private Treatment treatment(String name) {
        final Treatment treatment = treatments.get(name);
        Preconditions.checkState(treatment != null, "no treatment with name %s defined", name);
        return treatment;
    }

    /**
     * Saves the experiment and all changes made to it
     */
    public Experiment save() {
        if (created == null) {
            created = DateTime.now(DateTimeZone.UTC);
            modified = created;
        } else {
            modified = DateTime.now(DateTimeZone.UTC);
        }
        store.save(this);
        return this;
    }

    /**
     * Deletes the experiment and all things associated with it
     */
    public void delete() {
        store.delete(name);
    }

    /**
     * Allocates bins to a treatment
     * @param treatmentName The treatment
     * @param size The number of bins
     */
    public Experiment allocate(String treatmentName, int size) {
        allocations.allocate(treatment(treatmentName), size);
        return this;
    }

    /**
     * De-allocates bins from a treatment
     * @param treatmentName The treatment
     * @param size The number of bins
     */
    public Experiment deallocate(String treatmentName, int size) {
        allocations.deallocate(treatment(treatmentName), size);
        return this;
    }

    /**
     * Reallocates bins from one treatment to another
     * @param sourceTreatmentName The source treatment
     * @param destinationTreatmentName The destination treatment
     * @param size The number of bins
     */
    public Experiment reallocate(String sourceTreatmentName, String destinationTreatmentName, int size) {
        allocations.reallocate(
            treatment(sourceTreatmentName),
            treatment(destinationTreatmentName),
            size
        );

        return this;
    }

    private int identityToBin(Identity identity) {
        return (int) (FastMath.abs(identity.getHash(seed)) % Allocations.NUM_BINS);
    }

    /**
     * Returns treatment for an identity
     * @param identity The identity
     * @return the treatment assigned to given identity
     */
    public Treatment getTreatment(Identity identity) {
        return allocations.getTreatment(identityToBin(identity));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Experiment)) {
            return false;
        }

        final Experiment other = (Experiment) obj;
        return
            Objects.equal(name, other.name);
    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("name", name)
                .add("description", description)
                .add("identityType", identityType)
                .add("active", active)
                .add("created", created)
                .add("modified", modified)
                .add("activated", activated)
                .add("deactivated", deactivated)
                .add("seed", seed)
                .toString();
    }

    /**
     * Builder for building Experiment inside store
     */
    public static class Builder {
        private final ExperimentsStore store;
        private final String name;
        private String description;
        private String identityType;
        private boolean active;
        private DateTime created = DateTime.now(DateTimeZone.UTC);
        private DateTime modified  = DateTime.now(DateTimeZone.UTC);
        private DateTime activated;
        private DateTime deactivated;
        private final Map<String, Treatment> treatments;
        private final List<TreatmentOverride> overrides;
        private final List<Allocation> allocations;

        Builder(ExperimentsStore store, String name) {
            this.store = store;
            this.name = name;
            treatments = Maps.newHashMap();
            overrides = Lists.newArrayList();
            allocations = Lists.newArrayList();
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder identityType(String identityType) {
            this.identityType = identityType;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder created(DateTime created) {
            this.created = created;
            return this;
        }

        public Builder modified(DateTime modified) {
            this.modified = modified;
            return this;
        }

        public Builder activated(DateTime activated) {
            this.activated = activated;
            return this;
        }

        public Builder deactivated(DateTime deactivated) {
            this.deactivated = deactivated;
            return this;
        }

        private Treatment getTreatment(String name) {
            final Treatment treatment = treatments.get(name);
            Preconditions.checkState(treatment != null, "treatment with name %s must be defined first", name);
            return treatment;
        }

        public Builder addTreatment(String name, String description) {
            treatments.put(name, new Treatment(name, description));
            return this;
        }

        public Builder addOverride(String identity, long hash, String treatmentName) {
            overrides.add(new TreatmentOverride(identity, hash, getTreatment(treatmentName)));
            return this;
        }

        public Builder addAllocation(String treatmentName, int offset, int size) {
            allocations.add(new Allocation(getTreatment(treatmentName), offset, size));
            return this;
        }

        public Experiment build() {
            return new Experiment(
                store,
                name,
                description,
                identityType,
                active,
                created,
                modified,
                activated,
                deactivated,
                treatments,
                overrides,
                allocations
            );
        }
    }
}
