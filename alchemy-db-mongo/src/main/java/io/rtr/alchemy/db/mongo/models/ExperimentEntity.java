package io.rtr.alchemy.db.mongo.models;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import io.rtr.alchemy.db.Ordering;
import io.rtr.alchemy.models.Allocation;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Treatment;
import io.rtr.alchemy.models.TreatmentOverride;

import org.joda.time.DateTime;

import java.util.List;

import javax.annotation.Nullable;

/**
 * An entity that mirrors Experiment
 *
 * @see io.rtr.alchemy.models.Experiment
 */
@Entity(value = "Experiments", useDiscriminator = false)
public class ExperimentEntity {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ACTIVE = "active";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_MODIFIED = "modified";
    public static final String FIELD_ACTIVATED = "activated";
    public static final String FIELD_DEACTIVATED = "deactivated";
    private static final TreatmentMapper TREATMENT_MAPPER = new TreatmentMapper();
    private static final AllocationMapper ALLOCATION_MAPPER = new AllocationMapper();
    private static final TreatmentOverrideMapper TREATMENT_OVERRIDE_MAPPER =
            new TreatmentOverrideMapper();

    @Id public String name;
    public int seed;
    public String description;
    public String filter;
    public List<String> hashAttributes;
    @Indexed public boolean active;
    public long revision;
    public DateTime created;
    public DateTime modified;
    public DateTime activated;
    public DateTime deactivated;
    public List<TreatmentEntity> treatments;
    public List<AllocationEntity> allocations;
    public List<TreatmentOverrideEntity> overrides;

    // Required by Morphia
    @SuppressWarnings("unused")
    private ExperimentEntity() {}

    private ExperimentEntity(final Experiment experiment) {
        name = experiment.getName();
        seed = experiment.getSeed();
        description = experiment.getDescription();
        active = experiment.isActive();
        created = experiment.getCreated();
        modified = experiment.getModified();
        activated = experiment.getActivated();
        deactivated = experiment.getDeactivated();
        filter = experiment.getFilter().toString();
        hashAttributes = Lists.newArrayList(experiment.getHashAttributes());
        treatments =
                Lists.newArrayList(
                        Collections2.transform(experiment.getTreatments(), TREATMENT_MAPPER));
        allocations = Lists.transform(experiment.getAllocations(), ALLOCATION_MAPPER);
        overrides = Lists.transform(experiment.getOverrides(), TREATMENT_OVERRIDE_MAPPER);
    }

    public static ExperimentEntity from(final Experiment experiment) {
        return new ExperimentEntity(experiment);
    }

    public static String getFieldName(final Ordering.Field field) {
        switch (field) {
            case NAME:
                return ExperimentEntity.FIELD_NAME;
            case DESCRIPTION:
                return ExperimentEntity.FIELD_DESCRIPTION;
            case ACTIVE:
                return ExperimentEntity.FIELD_ACTIVE;
            case CREATED:
                return ExperimentEntity.FIELD_CREATED;
            case MODIFIED:
                return ExperimentEntity.FIELD_MODIFIED;
            case ACTIVATED:
                return ExperimentEntity.FIELD_ACTIVATED;
            case DEACTIVATED:
                return ExperimentEntity.FIELD_DEACTIVATED;
            default:
                throw new IllegalArgumentException(
                        String.format(
                                "Unsupported ordering field: %s (%s)", field, field.getName()));
        }
    }

    public Experiment toExperiment(final Experiment.Builder builder) {
        builder.seed(seed)
                .description(description)
                .filter(filter)
                .hashAttributes(Sets.newLinkedHashSet(hashAttributes))
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
                builder.addOverride(override.name, override.filter, override.treatment);
            }
        }

        return builder.build();
    }

    private static class TreatmentMapper implements Function<Treatment, TreatmentEntity> {
        @Nullable
        @Override
        public TreatmentEntity apply(@Nullable final Treatment input) {
            return input == null ? null : TreatmentEntity.from(input);
        }
    }

    private static class AllocationMapper implements Function<Allocation, AllocationEntity> {
        @Nullable
        @Override
        public AllocationEntity apply(@Nullable final Allocation input) {
            return input == null ? null : AllocationEntity.from(input);
        }
    }

    private static class TreatmentOverrideMapper
            implements Function<TreatmentOverride, TreatmentOverrideEntity> {
        @Nullable
        @Override
        public TreatmentOverrideEntity apply(@Nullable final TreatmentOverride input) {
            return input == null ? null : TreatmentOverrideEntity.from(input);
        }
    }
}
