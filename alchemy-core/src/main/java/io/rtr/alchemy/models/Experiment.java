package io.rtr.alchemy.models;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.identities.IdentityBuilder;
import org.apache.commons.math3.util.FastMath;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.annotation.Nullable;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a collection of user experiences being tested
 */

public class Experiment {
    private static final Set<String> EMPTY_SET = Sets.newLinkedHashSet();
    private static final Function<TreatmentOverride, String> TREATMENT_INDEXER =
        new Function<TreatmentOverride, String>() {
            @Nullable
            @Override
            public String apply(@Nullable TreatmentOverride input) {
                return input != null ? input.getName() : null;
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
    private Experiment(Experiments owner,
                       String name,
                       int seed,
                       String description,
                       FilterExpression filter,
                       Set<String> hashAttributes,
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
        this.filter = Objects.firstNonNull(filter, FilterExpression.alwaysTrue());
        this.hashAttributes = Objects.firstNonNull(hashAttributes, EMPTY_SET);
        this.active = active;
        this.created = created;
        this.modified = modified;
        this.activated = activated;
        this.deactivated = deactivated;
        this.treatments = new ConcurrentHashMap<>(treatments);
        this.overrides = new ConcurrentHashMap<>(Maps.uniqueIndex(overrides, TREATMENT_INDEXER));
        this.allocations = new Allocations(allocations);
        this.seed = seed;
    }

    // used when creating a new experiment
    protected Experiment(Experiments owner,
                         String name) throws ValidationException {
        this.owner = owner;
        this.name = new NameValidation().validate(name);
        this.filter = FilterExpression.alwaysTrue();
        this.hashAttributes = EMPTY_SET;
        this.allocations = new Allocations();
        this.treatments = Maps.newConcurrentMap();
        this.overrides = Maps.newConcurrentMap();
        this.seed = (int) IdentityBuilder.seed(0).putString(name).hash();
    }

    public static Experiment copyOf(Experiment experiment) throws ValidationException {
        return  experiment != null ? new Experiment(experiment) : null;
    }

    private Experiment(Experiment toCopy) throws ValidationException {
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
        for (final TreatmentOverride override : toCopy.getOverrides()) {
            final Treatment treatment = this.treatments.get(override.getTreatment().getName());
            final TreatmentOverride newOverride =  new TreatmentOverride(override.getName(), override.getFilter(), treatment);
            overrides.put(override.getName(), newOverride);
        }

        this.seed = toCopy.seed;
        this.description = toCopy.description;
        this.filter = toCopy.filter;
        this.hashAttributes = Sets.newLinkedHashSet(toCopy.getHashAttributes());
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

    public FilterExpression getFilter() {
        return filter;
    }

    public Set<String> getHashAttributes() {
        return Collections.unmodifiableSet(hashAttributes);
    }

    public Experiment setFilter(FilterExpression filter) {
        this.filter = filter;
        return this;
    }

    public Experiment setHashAttributes(Set<String> hashAttributes) {
        if (hashAttributes == null) {
            this.hashAttributes = EMPTY_SET;
        } else {
            this.hashAttributes = Sets.newLinkedHashSet(hashAttributes);
        }
        return this;
    }

    public Experiment setHashAttributes(String ... hashAttributes) {
        if (hashAttributes == null) {
            this.hashAttributes = EMPTY_SET;
        } else {
            this.hashAttributes = Sets.newLinkedHashSet(Lists.newArrayList(hashAttributes));
        }
        return this;
    }

    /**
     * Sets the seed used to compute hashes from identities. WARNING: Changing this value will change what users are assigned to what treatments
     */
    public Experiment setSeed(int seed) {
        this.seed = seed;
        return this;
    }

    public int getSeed() {
        return seed;
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

    /**
     * Gets all allocations defined on this experiment
     */
    public List<Allocation> getAllocations() {
        return Collections.unmodifiableList(allocations.getAllocations());
    }

    /**
     * Gets all treatments defined on this experiment
     */
    public List<Treatment> getTreatments() {
        return Collections.unmodifiableList(Lists.newArrayList(treatments.values()));
    }

    /**
     * Get a treatment with the given name
     */
    public Treatment getTreatment(String treatmentName) {
        return treatments.get(treatmentName);
    }

    /**
     * Gets all overrides defined on this experiment
     */
    public List<TreatmentOverride> getOverrides() {
        return ImmutableList.copyOf(overrides.values());
    }

    /**
     * Gets the assigned override for a given name
     * @param overrideName The name
     */
    public TreatmentOverride getOverride(String overrideName) {
        return overrides.get(overrideName);
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
    public Experiment addTreatment(String name) throws ValidationException {
        treatments.put(name, new Treatment(name));
        return this;
    }

    /**
     * Adds a treatment
     * @param name The name
     * @param description The description
     */
    public Experiment addTreatment(String name, String description) throws ValidationException {
        treatments.put(name, new Treatment(name, description));
        return this;
    }

    /**
     * Removes all treatments
     */
    public Experiment clearTreatments() {
        final List<Treatment> toRemove = Lists.newArrayList(treatments.values());
        for (final Treatment treatment : toRemove) {
            removeTreatment(treatment.getName());
        }

        return this;
    }

    /**
     * Removes all overrides
     */
    public Experiment clearOverrides() {
        overrides.clear();
        return this;
    }

    /**
     * Add a treatment override for an identity
     * @param treatmentName The treatment an identity should receive
     * @param overrideName The name of the override
     * @param filter A filter expression that describes which attributes this override should apply for
     */
    public Experiment addOverride(String overrideName, String treatmentName, String filter) throws ValidationException {
        final FilterExpression filterExp = FilterExpression.of(filter);
        final TreatmentOverride override = new TreatmentOverride(overrideName, filterExp, treatment(treatmentName));
        overrides.put(overrideName, override);

        return this;
    }

    /**
     * Remove an override
     * @param overrideName The name of the override to remove
     */
    public Experiment removeOverride(String overrideName) {
        overrides.remove(overrideName);
        return this;
    }

    /**
     * Removes all overrides for a given treatment
     * @param treatmentName The treatment to remove overrides for
     */
    public Experiment removeOverrides(String treatmentName) {
        final Treatment treatment = treatments.get(treatmentName);

        if (treatment == null) {
            return this;
        }

        final Iterator<Entry<String, TreatmentOverride>> iterator = overrides.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, TreatmentOverride> entry = iterator.next();
            if (entry.getValue().getTreatment().equals(treatment)) {
                iterator.remove();
            }
        }

        return this;
    }

    /**
     * Removes a treatment
     * @param name The treatment
     */
    public Experiment removeTreatment(String name) {
        final Treatment treatment = treatments.get(name);
        if (treatment == null) {
            return this;
        }

        removeOverrides(name);
        allocations.deallocate(treatment, Allocations.NUM_BINS);
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

    /**
     * Removes all allocations
     */
    public Experiment deallocateAll() {
        allocations.clear();
        return this;
    }

    private int identityToBin(Identity identity, AttributesMap attributes) {
        return (int) (FastMath.abs(identity.computeHash(seed, hashAttributes ,attributes)) % Allocations.NUM_BINS);
    }

    /**
     * Returns treatment for an identity
     * @param identity The identity
     * @return the treatment assigned to given identity
     */
    public Treatment getTreatment(Identity identity, AttributesMap attributes) {
        return allocations.getTreatment(identityToBin(identity, attributes));
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
                .add("filter", filter)
                .add("hashAttributes", hashAttributes)
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
        private int seed;
        private String description;
        private FilterExpression filter;
        private Set<String> hashAttributes;
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

        public Builder filter(String filter) {
            this.filter = FilterExpression.of(filter);
            return this;
        }

        public Builder hashAttributes(String ... hashAttributes) {
            this.hashAttributes = Sets.newLinkedHashSet(Arrays.asList(hashAttributes));
            return this;
        }

        public Builder hashAttributes(Set<String> hashAttributes) {
            this.hashAttributes = Sets.newLinkedHashSet(hashAttributes);
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

        public Builder seed(int seed) {
            this.seed = seed;
            return this;
        }

        private Treatment getTreatment(String name) {
            final Treatment treatment = treatments.get(name);
            Preconditions.checkState(treatment != null, "treatment with name %s must be defined first", name);
            return treatment;
        }

        public Builder addTreatment(String name, String description) throws ValidationException {
            treatments.put(name, new Treatment(name, description));
            return this;
        }

        public Builder addOverride(String name, String filter, String treatmentName) throws ValidationException {
            overrides.add(new TreatmentOverride(name, FilterExpression.of(filter), getTreatment(treatmentName)));
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
                allocations
            );
        }
    }
}
