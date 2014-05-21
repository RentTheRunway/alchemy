package com.rtr.alchemy.db.mongo.models;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.rtr.alchemy.db.mongo.util.DateTimeConverter;
import com.rtr.alchemy.models.Allocation;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Treatment;
import com.rtr.alchemy.models.TreatmentOverride;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Set;

/**
 * An entity that mirrors Experiment
 * @see com.rtr.alchemy.models.Experiment
 */
@Entity(value = "Experiments", noClassnameStored = true)
@Converters(DateTimeConverter.class)
public class ExperimentEntity {
    private static final TreatmentMapper TREATMENT_MAPPER = new TreatmentMapper();
    private static final AllocationMapper ALLOCATION_MAPPER = new AllocationMapper();
    private static final TreatmentOverrideMapper TREATMENT_OVERRIDE_MAPPER = new TreatmentOverrideMapper();

    @Id
    public String name;
    public int seed;
    public String description;
    public Set<String> segments;

    @Indexed
    public boolean active;

    public long revision;
    public DateTime created;
    public DateTime modified;
    public DateTime activated;
    public DateTime deactivated;

    @Embedded
    public List<TreatmentEntity> treatments;

    @Embedded
    public List<AllocationEntity> allocations;

    @Embedded
    public List<TreatmentOverrideEntity> overrides;

    public static ExperimentEntity from(Experiment experiment) {
        return new ExperimentEntity(experiment);
    }

    public Experiment toExperiment(Experiment.Builder builder) {
        builder
            .seed(seed)
            .description(description)
            .segments(segments)
            .created(created)
            .modified(modified)
            .activated(activated)
            .deactivated(deactivated)
            .active(active);

        if (treatments != null) {
            for (final TreatmentEntity treatment : treatments) {
                builder.addTreatment(treatment.name, treatment.description);
            }
        }

        if (allocations != null) {
            for (final AllocationEntity allocation : allocations) {
                builder.addAllocation(allocation.treatment, allocation.offset, allocation.size);
            }
        }

        if (overrides != null) {
            for (final TreatmentOverrideEntity override : overrides) {
                builder.addOverride(override.name, override.hash, override.treatment);
            }
        }

        return builder.build();
    }

    // Required by Morphia
    private ExperimentEntity() { }

    private ExperimentEntity(Experiment experiment) {
        name = experiment.getName();
        seed = experiment.getSeed();
        description = experiment.getDescription();
        active = experiment.isActive();
        created = experiment.getCreated();
        modified = experiment.getModified();
        activated = experiment.getActivated();
        deactivated = experiment.getDeactivated();
        segments = experiment.getSegments();
        treatments = Lists.transform(experiment.getTreatments(), TREATMENT_MAPPER);
        allocations = Lists.transform(experiment.getAllocations(), ALLOCATION_MAPPER);
        overrides = Lists.transform(experiment.getOverrides(),TREATMENT_OVERRIDE_MAPPER);
    }

    private static class TreatmentMapper implements Function<Treatment, TreatmentEntity> {
        @Nullable
        @Override
        public TreatmentEntity apply(@Nullable Treatment input) {
            return input == null ? null : TreatmentEntity.from(input);
        }
    }

    private static class AllocationMapper implements Function<Allocation, AllocationEntity> {
        @Nullable
        @Override
        public AllocationEntity apply(@Nullable Allocation input) {
            return input == null ? null : AllocationEntity.from(input);
        }
    }

    private static class TreatmentOverrideMapper implements Function<TreatmentOverride, TreatmentOverrideEntity> {
        @Nullable
        @Override
        public TreatmentOverrideEntity apply(@Nullable TreatmentOverride input) {
            return input == null ? null : TreatmentOverrideEntity.from(input);
        }
    }
}