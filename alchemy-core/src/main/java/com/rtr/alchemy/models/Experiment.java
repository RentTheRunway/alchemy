package com.rtr.alchemy.models;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.IdentityBuilder;
import org.apache.commons.math3.util.FastMath;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a collection of user experiences being tested
 */

public class Experiment {
    private final Object lock = new Object();
    private final Experiments owner;
    private final String name;
    private final Allocations allocations;
    private final Map<String, Treatment> treatments;
    private final Map<String, TreatmentOverride> overrides;
    private final Map<Long, TreatmentOverride> overridesByHash;
    private final int seed;
    private volatile String description;
    private volatile String identityType;
    private volatile boolean active;
    private volatile DateTime created;
    private volatile DateTime modified;
    private volatile DateTime activated;
    private volatile DateTime deactivated;

    // used by Builder when loading experiment from store
    private Experiment(Experiments owner,
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
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.identityType = identityType;
        this.active = active;
        this.created = created;
        this.modified = modified;
        this.activated = activated;
        this.deactivated = deactivated;

        this.treatments = treatments;
        for (final Treatment treatment : treatments.values()) {
            this.treatments.put(treatment.getName(), treatment);
        }

        this.overrides = Maps.newConcurrentMap();
        this.overridesByHash = Maps.newConcurrentMap();
        for (final TreatmentOverride override : overrides) {
            this.overridesByHash.put(override.getHash(), override);
            this.overrides.put(override.getName(), override);
        }

        this.allocations = new Allocations(allocations);
        this.seed = (int) IdentityBuilder.seed(0).putString(name).hash();
    }

    // used when creating a new experiment
    protected Experiment(Experiments owner,
                         String name) {
        this.owner = owner;
        this.name = name;
        this.allocations = new Allocations();
        this.treatments = Maps.newConcurrentMap();
        this.overrides = Maps.newConcurrentMap();
        this.overridesByHash = Maps.newConcurrentMap();
        this.seed = (int) IdentityBuilder.seed(0).putString(name).hash();
    }

    public static Experiment copyOf(Experiment experiment) {
        return  experiment != null ? new Experiment(experiment) : null;
    }

    private Experiment(Experiment toCopy) {
        this.owner = toCopy.owner;
        this.name = toCopy.name;

        this.treatments = Maps.newConcurrentMap();
        for (final Treatment treatment : toCopy.getTreatments()) {
            this.treatments.put(treatment.getName(), new Treatment(treatment.getName(), treatment.getDescription()));
        }

        final List<Allocation> allocations = Lists.newArrayList();
        for (final Allocation allocation : toCopy.getAllocations()) {
            final Treatment treatment = this.treatments.get(allocation.getTreatment().getName());
            allocations.add(new Allocation(treatment, allocation.getOffset(), allocation.getSize()));
        }

        this.allocations = new Allocations(allocations);

        this.overrides = Maps.newConcurrentMap();
        this.overridesByHash = Maps.newConcurrentMap();
        for (final TreatmentOverride override : toCopy.getOverrides()) {
            final Treatment treatment = this.treatments.get(override.getTreatment().getName());
            final TreatmentOverride newOverride =  new TreatmentOverride(override.getName(), override.getHash(), treatment);
            overrides.put(override.getName(), newOverride);
            overridesByHash.put(override.getHash(), newOverride);
        }

        this.seed = toCopy.seed;
        this.description = toCopy.description;
        this.identityType = toCopy.identityType;
        this.active = toCopy.active;
        this.created = toCopy.created;
        this.modified = toCopy.modified;
        this.activated = toCopy.activated;
        this.deactivated = toCopy.deactivated;
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
    public List<Allocation> getAllocations() {
        synchronized (lock) {
            return ImmutableList.copyOf(allocations.getAllocations());
        }
    }

    /**
     * Gets all treatments defined on this experiment
     */
    public List<Treatment> getTreatments() {
        synchronized (lock) {
            return ImmutableList.copyOf(treatments.values());
        }
    }

    /**
     * Get a treatment with the given name
     */
    public Treatment getTreatment(String treatmentName) {
        synchronized (lock) {
            return treatments.get(treatmentName);
        }
    }

    /**
     * Gets all overrides defined on this experiment
     */
    public List<TreatmentOverride> getOverrides() {
        synchronized (lock) {
            return ImmutableList.copyOf(overrides.values());
        }
    }

    /**
     * Gets the assigned override for a given identity
     * @param identity The identity
     */
    public TreatmentOverride getOverride(Identity identity) {
        synchronized (lock) {
            return overridesByHash.get(identity.getHash(seed));
        }
    }

    /**
     * Gets the assigned override for a given name
     * @param overrideName The name
     */
    public TreatmentOverride getOverride(String overrideName) {
        synchronized (lock) {
            return overrides.get(overrideName);
        }
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
        synchronized (lock) {
            treatments.put(name, new Treatment(name));
        }
        return this;
    }

    /**
     * Adds a treatment
     * @param name The name
     * @param description The description
     */
    public Experiment addTreatment(String name, String description) {
        synchronized (lock) {
            treatments.put(name, new Treatment(name, description));
        }
        return this;
    }

    /**
     * Removes all treatments
     */
    public Experiment clearTreatments() {
        synchronized (lock) {
            final List<Treatment> toRemove = Lists.newArrayList(treatments.values());
            for (final Treatment treatment : toRemove) {
                removeTreatment(treatment.getName());
            }
        }

        return this;
    }

    /**
     * Removes all overrides
     */
    public Experiment clearOverrides() {
        synchronized (lock) {
            overrides.clear();
            overridesByHash.clear();
        }

        return this;
    }

    /**
     * Add a treatment override for an identity
     * @param treatmentName The treatment an identity should receive
     * @param overrideName The name of the override
     * @param identity The identity
     */
    public Experiment addOverride(String overrideName, String treatmentName, Identity identity) {
        synchronized (lock) {
            final Long hash = identity.getHash(seed);
            final TreatmentOverride override = new TreatmentOverride(overrideName, hash, treatment(treatmentName));
            final TreatmentOverride replaced = overridesByHash.put(hash, override);
            if (replaced != null) {
                overrides.remove(replaced.getName());
            }
            overrides.put(overrideName, override);
        }
        return this;
    }

    /**
     * Remove an override
     * @param identity The identities to remove the override for
     */
    public Experiment removeOverride(Identity identity) {
        synchronized (lock) {
            final TreatmentOverride removed = overridesByHash.remove(identity.getHash(seed));
            if (removed != null) {
                overrides.remove(removed.getName());
            }
        }

        return this;
    }

    /**
     * Remove an override
     * @param overrideName The name of the override to remove
     */
    public Experiment removeOverride(String overrideName) {
        synchronized (lock) {
            final  TreatmentOverride removed = overrides.remove(overrideName);
            if (removed != null) {
                overridesByHash.remove(removed.getHash());
            }
        }
        return this;
    }

    /**
     * Removes all overrides for a given treatment
     * @param treatmentName The treatment to remove overrides for
     */
    public Experiment removeOverrides(String treatmentName) {
        synchronized (lock) {
            final Treatment treatment = treatments.get(treatmentName);

            if (treatment == null) {
                return this;
            }

            final Iterator<Entry<Long, TreatmentOverride>> iterator = overridesByHash.entrySet().iterator();
            while (iterator.hasNext()) {
                final Entry<Long, TreatmentOverride> entry = iterator.next();
                if (entry.getValue().getTreatment().equals(treatment)) {
                    iterator.remove();
                    overrides.remove(entry.getValue().getName());
                }
            }
        }

        return this;
    }

    /**
     * Removes a treatment
     * @param name The treatment
     */
    public Experiment removeTreatment(String name) {
        synchronized (lock) {
            final Treatment treatment = treatments.get(name);
            if (treatment == null) {
                return this;
            }

            removeOverrides(name);
            allocations.deallocate(treatment, Allocations.NUM_BINS);
            treatments.remove(name);
        }

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
        owner.save(this);

        return this;
    }

    /**
     * Deletes the experiment and all things associated with it
     */
    public void delete() {
        owner.delete(name);
    }

    /**
     * Allocates bins to a treatment
     * @param treatmentName The treatment
     * @param size The number of bins
     */
    public Experiment allocate(String treatmentName, int size) {
        synchronized (lock) {
            allocations.allocate(treatment(treatmentName), size);
        }
        return this;
    }

    /**
     * De-allocates bins from a treatment
     * @param treatmentName The treatment
     * @param size The number of bins
     */
    public Experiment deallocate(String treatmentName, int size) {
        synchronized (lock) {
            allocations.deallocate(treatment(treatmentName), size);
        }
        return this;
    }

    /**
     * Reallocates bins from one treatment to another
     * @param sourceTreatmentName The source treatment
     * @param destinationTreatmentName The destination treatment
     * @param size The number of bins
     */
    public Experiment reallocate(String sourceTreatmentName, String destinationTreatmentName, int size) {
        synchronized (lock) {
            allocations.reallocate(
                treatment(sourceTreatmentName),
                treatment(destinationTreatmentName),
                size
            );
        }

        return this;
    }

    /**
     * Removes all allocations
     */
    public Experiment deallocateAll() {
        synchronized (lock) {
            allocations.clear();
        }
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
        synchronized (lock) {
            return allocations.getTreatment(identityToBin(identity));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Experiment)) {
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

    public static class BuilderFactory {
        private final Experiments owner;

        BuilderFactory(Experiments owner) {
            this.owner = owner;
        }

        public Builder createBuilder(String experimentName) {
            return new Builder(owner, experimentName);
        }
    }

    /**
     * Builder for building Experiment inside store
     */
    public static class Builder {
        private final Experiments owner;
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

        Builder(Experiments owner, String name) {
            this.owner = owner;
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
                owner,
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
