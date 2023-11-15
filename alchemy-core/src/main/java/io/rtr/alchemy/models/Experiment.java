package io.rtr.alchemy.models;

import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.identities.IdentityBuilder;

import org.apache.commons.math3.util.FastMath;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;
import javax.validation.ValidationException;

/** Represents a collection of user experiences being tested */
public class Experiment implements Named {
    private static final Set<String> EMPTY_SET = new HashSet<>();
    private static final Function<TreatmentOverride, String> TREATMENT_INDEXER =
            new Function<>() {
                @Nullable
                @Override
                public String apply(@Nullable final TreatmentOverride input) {
                    return Optional.ofNullable(input).map(TreatmentOverride::getName).orElse(null);
                }
            };

    private final Experiments owner;
    private final String name;
    private final Allocations allocations;
    private final Map<String, Treatment> treatments;
    private final Map<String, TreatmentOverride> overrides;
    private int seed;
    private String description;
    private FilterExpression filter;
    private Set<String> hashAttributes;
    private boolean active;
    private DateTime created;
    private DateTime modified;
    private DateTime activated;
    private DateTime deactivated;

    // used by Builder when loading experiment from store
    private Experiment(
            final Experiments owner,
            final String name,
            final int seed,
            final String description,
            final FilterExpression filter,
            final Set<String> hashAttributes,
            final boolean active,
            final DateTime created,
            final DateTime modified,
            final DateTime activated,
            final DateTime deactivated,
            final Map<String, Treatment> treatments,
            final Iterable<TreatmentOverride> overrides,
            final Iterable<Allocation> allocations) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.filter = Optional.ofNullable(filter).orElse(FilterExpression.alwaysTrue());
        this.hashAttributes = Optional.ofNullable(hashAttributes).orElse(EMPTY_SET);
        this.active = active;
        this.created = created;
        this.modified = modified;
        this.activated = activated;
        this.deactivated = deactivated;
        this.treatments = new ConcurrentHashMap<>(treatments);
        this.overrides =
                StreamSupport.stream(overrides.spliterator(), false)
                        .collect(
                                Collectors.toConcurrentMap(TREATMENT_INDEXER, Function.identity()));
        this.allocations = new Allocations(allocations);
        this.seed = seed;
    }

    // used when creating a new experiment
    protected Experiment(final Experiments owner, final String name) {
        this.owner = owner;
        this.name = name;
        this.filter = FilterExpression.alwaysTrue();
        this.hashAttributes = EMPTY_SET;
        this.allocations = new Allocations();
        this.treatments = new ConcurrentHashMap<>();
        this.overrides = new ConcurrentHashMap<>();
        this.seed = (int) IdentityBuilder.seed(0).putString(name).hash();
    }

    private Experiment(final Experiment toCopy) throws ValidationException {
        this.owner = toCopy.owner;
        this.name = toCopy.name;

        this.treatments = new ConcurrentHashMap<>();
        for (final Treatment treatment : toCopy.getTreatments()) {
            this.treatments.put(
                    treatment.getName(),
                    new Treatment(treatment.getName(), treatment.getDescription()));
        }

        final List<Allocation> allocations = new ArrayList<>();
        for (final Allocation allocation : toCopy.getAllocations()) {
            final Treatment treatment = this.treatments.get(allocation.getTreatment().getName());
            allocations.add(
                    new Allocation(treatment, allocation.getOffset(), allocation.getSize()));
        }

        this.allocations = new Allocations(allocations);

        this.overrides = new ConcurrentHashMap<>();
        for (final TreatmentOverride override : toCopy.getOverrides()) {
            final Treatment treatment = this.treatments.get(override.getTreatment().getName());
            final TreatmentOverride newOverride =
                    new TreatmentOverride(override.getName(), override.getFilter(), treatment);
            overrides.put(override.getName(), newOverride);
        }

        this.seed = toCopy.seed;
        this.description = toCopy.description;
        this.filter = toCopy.filter;
        this.hashAttributes = new LinkedHashSet<>(toCopy.getHashAttributes());
        this.active = toCopy.active;
        this.created = toCopy.created;
        this.modified = toCopy.modified;
        this.activated = toCopy.activated;
        this.deactivated = toCopy.deactivated;
    }

    public static Experiment copyOf(final Experiment experiment) throws ValidationException {
        return experiment != null ? new Experiment(experiment) : null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Experiment setDescription(final String description) {
        this.description = description;
        return this;
    }

    public FilterExpression getFilter() {
        return filter;
    }

    public Experiment setFilter(final FilterExpression filter) {
        this.filter = filter;
        return this;
    }

    public Set<String> getHashAttributes() {
        return Collections.unmodifiableSet(hashAttributes);
    }

    public Experiment setHashAttributes(final Set<String> hashAttributes) {
        if (hashAttributes == null) {
            this.hashAttributes = EMPTY_SET;
        } else {
            this.hashAttributes = new LinkedHashSet<>(hashAttributes);
        }
        return this;
    }

    public Experiment setHashAttributes(final String... hashAttributes) {
        if (hashAttributes == null) {
            this.hashAttributes = EMPTY_SET;
        } else {
            this.hashAttributes = new LinkedHashSet<>(List.of(hashAttributes));
        }
        return this;
    }

    public int getSeed() {
        return seed;
    }

    /**
     * Sets the seed used to compute hashes from identities. WARNING: Changing this value will
     * change what users are assigned to what treatments
     */
    public Experiment setSeed(final int seed) {
        this.seed = seed;
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

    public DateTime getActivated() {
        return activated;
    }

    public DateTime getDeactivated() {
        return deactivated;
    }

    /** Gets all allocations defined on this experiment */
    public List<Allocation> getAllocations() {
        return Collections.unmodifiableList(allocations.getAllocations());
    }

    /** Gets all treatments defined on this experiment */
    public List<Treatment> getTreatments() {
        return List.copyOf(treatments.values());
    }

    /** Get a treatment with the given name */
    public Treatment getTreatment(final String treatmentName) {
        return treatments.get(treatmentName);
    }

    /** Gets all overrides defined on this experiment */
    public List<TreatmentOverride> getOverrides() {
        return List.copyOf(overrides.values());
    }

    /**
     * Gets the assigned override for a given name
     *
     * @param overrideName The name
     */
    public TreatmentOverride getOverride(final String overrideName) {
        return overrides.get(overrideName);
    }

    /** Activates the experiments, enabling all treatments */
    public Experiment activate() {
        if (active) {
            return this;
        }

        active = true;
        activated = DateTime.now(DateTimeZone.UTC);
        return this;
    }

    /** Deactivates the experiment, disabling all treatments */
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
     *
     * @param name The name
     */
    public Experiment addTreatment(final String name) throws ValidationException {
        treatments.put(name, new Treatment(name));
        return this;
    }

    /**
     * Adds a treatment
     *
     * @param name The name
     * @param description The description
     */
    public Experiment addTreatment(final String name, final String description)
            throws ValidationException {
        treatments.put(name, new Treatment(name, description));
        return this;
    }

    /** Removes all treatments */
    public Experiment clearTreatments() {
        final List<Treatment> toRemove = List.copyOf(treatments.values());
        for (final Treatment treatment : toRemove) {
            removeTreatment(treatment.getName());
        }

        return this;
    }

    /** Removes all overrides */
    public Experiment clearOverrides() {
        overrides.clear();
        return this;
    }

    /**
     * Add a treatment override for an identity
     *
     * @param treatmentName The treatment an identity should receive
     * @param overrideName The name of the override
     * @param filter A filter expression that describes which attributes this override should apply
     *     for
     */
    public Experiment addOverride(
            final String overrideName, final String treatmentName, final String filter)
            throws ValidationException {
        final FilterExpression filterExp = FilterExpression.of(filter);
        final TreatmentOverride override =
                new TreatmentOverride(overrideName, filterExp, treatment(treatmentName));
        overrides.put(overrideName, override);

        return this;
    }

    /**
     * Remove an override
     *
     * @param overrideName The name of the override to remove
     */
    public Experiment removeOverride(final String overrideName) {
        overrides.remove(overrideName);
        return this;
    }

    /**
     * Removes all overrides for a given treatment
     *
     * @param treatmentName The treatment to remove overrides for
     */
    public Experiment removeOverrides(final String treatmentName) {
        final Treatment treatment = treatments.get(treatmentName);

        if (treatment == null) {
            return this;
        }

        overrides.entrySet().removeIf(entry -> entry.getValue().getTreatment().equals(treatment));

        return this;
    }

    /**
     * Removes a treatment
     *
     * @param name The treatment
     */
    public Experiment removeTreatment(final String name) {
        final Treatment treatment = treatments.get(name);
        if (treatment == null) {
            return this;
        }

        removeOverrides(name);
        allocations.deallocate(treatment, Allocations.NUM_BINS);
        treatments.remove(name);

        return this;
    }

    private Treatment treatment(final String name) {
        final Treatment treatment = treatments.get(name);
        if (treatment == null) {
            throw new IllegalArgumentException(
                    String.format("no treatment with name %s defined", name));
        }
        return treatment;
    }

    /** Saves the experiment and all changes made to it */
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

    /** Deletes the experiment and all things associated with it */
    public void delete() {
        owner.delete(name);
    }

    /**
     * Allocates bins to a treatment
     *
     * @param treatmentName The treatment
     * @param size The number of bins
     */
    public Experiment allocate(final String treatmentName, final int size) {
        allocations.allocate(treatment(treatmentName), size);

        return this;
    }

    /**
     * De-allocates bins from a treatment
     *
     * @param treatmentName The treatment
     * @param size The number of bins
     */
    public Experiment deallocate(final String treatmentName, final int size) {
        allocations.deallocate(treatment(treatmentName), size);
        return this;
    }

    /**
     * Reallocates bins from one treatment to another
     *
     * @param sourceTreatmentName The source treatment
     * @param destinationTreatmentName The destination treatment
     * @param size The number of bins
     */
    public Experiment reallocate(
            final String sourceTreatmentName,
            final String destinationTreatmentName,
            final int size) {
        allocations.reallocate(
                treatment(sourceTreatmentName), treatment(destinationTreatmentName), size);

        return this;
    }

    /** Removes all allocations */
    public Experiment deallocateAll() {
        allocations.clear();
        return this;
    }

    private int identityToBin(final Identity identity, final AttributesMap attributes) {
        return (int)
                (FastMath.abs(identity.computeHash(seed, hashAttributes, attributes))
                        % Allocations.NUM_BINS);
    }

    /**
     * Returns treatment for an identity
     *
     * @param identity The identity
     * @return the treatment assigned to given identity
     */
    public Treatment getTreatment(final Identity identity, final AttributesMap attributes) {
        return allocations.getTreatment(identityToBin(identity, attributes));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Experiment)) return false;
        final Experiment that = (Experiment) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Experiment.class.getSimpleName() + "[", "]")
                .add("owner=" + owner)
                .add("name='" + name + "'")
                .add("allocations=" + allocations)
                .add("treatments=" + treatments)
                .add("overrides=" + overrides)
                .add("seed=" + seed)
                .add("description='" + description + "'")
                .add("filter=" + filter)
                .add("hashAttributes=" + hashAttributes)
                .add("active=" + active)
                .add("created=" + created)
                .add("modified=" + modified)
                .add("activated=" + activated)
                .add("deactivated=" + deactivated)
                .toString();
    }

    public static class BuilderFactory {
        private final Experiments owner;

        BuilderFactory(final Experiments owner) {
            this.owner = owner;
        }

        public Builder createBuilder(final String experimentName) {
            return new Builder(owner, experimentName);
        }
    }

    /** Builder for building Experiment inside store */
    public static class Builder {
        private final Experiments owner;
        private final String name;
        private final Map<String, Treatment> treatments;
        private final List<TreatmentOverride> overrides;
        private final List<Allocation> allocations;
        private int seed;
        private String description;
        private FilterExpression filter;
        private Set<String> hashAttributes;
        private boolean active;
        private DateTime created = DateTime.now(DateTimeZone.UTC);
        private DateTime modified = DateTime.now(DateTimeZone.UTC);
        private DateTime activated;
        private DateTime deactivated;

        Builder(final Experiments owner, final String name) {
            this.owner = owner;
            this.name = name;
            treatments = new HashMap<>();
            overrides = new ArrayList<>();
            allocations = new ArrayList<>();
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder filter(final String filter) {
            this.filter = FilterExpression.of(filter);
            return this;
        }

        public Builder hashAttributes(final String... hashAttributes) {
            this.hashAttributes = new LinkedHashSet<>(Arrays.asList(hashAttributes));
            return this;
        }

        public Builder hashAttributes(final Set<String> hashAttributes) {
            this.hashAttributes = new LinkedHashSet<>(hashAttributes);
            return this;
        }

        public Builder active(final boolean active) {
            this.active = active;
            return this;
        }

        public Builder created(final DateTime created) {
            this.created = created;
            return this;
        }

        public Builder modified(final DateTime modified) {
            this.modified = modified;
            return this;
        }

        public Builder activated(final DateTime activated) {
            this.activated = activated;
            return this;
        }

        public Builder deactivated(final DateTime deactivated) {
            this.deactivated = deactivated;
            return this;
        }

        public Builder seed(final int seed) {
            this.seed = seed;
            return this;
        }

        private Treatment getTreatment(final String name) {
            final Treatment treatment = treatments.get(name);
            if (treatment == null) {
                throw new IllegalArgumentException(
                        String.format("treatment with name %s must be defined first", name));
            }
            return treatment;
        }

        public Builder addTreatment(final String name, final String description)
                throws ValidationException {
            treatments.put(name, new Treatment(name, description));
            return this;
        }

        public Builder addOverride(
                final String name, final String filter, final String treatmentName)
                throws ValidationException {
            overrides.add(
                    new TreatmentOverride(
                            name, FilterExpression.of(filter), getTreatment(treatmentName)));
            return this;
        }

        public Builder addAllocation(final String treatmentName, final int offset, final int size) {
            allocations.add(new Allocation(getTreatment(treatmentName), offset, size));
            return this;
        }

        public Experiment build() {
            return new Experiment(
                    owner,
                    name,
                    seed,
                    description,
                    filter,
                    hashAttributes,
                    active,
                    created,
                    modified,
                    activated,
                    deactivated,
                    treatments,
                    overrides,
                    allocations);
        }
    }
}
