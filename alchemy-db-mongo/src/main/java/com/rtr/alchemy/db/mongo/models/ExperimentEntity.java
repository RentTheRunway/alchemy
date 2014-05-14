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

import java.util.List;

/**
 * An entity that mirrors Experiment
 * @see com.rtr.alchemy.models.Experiment
 */
@Entity(value = "Experiments", noClassnameStored = true)
@Converters(DateTimeConverter.class)
public class ExperimentEntity {
    @Id
    public String name;
    public String description;
    public String identityType;

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
            .description(description)
            .identityType(identityType)
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
        description = experiment.getDescription();
        active = experiment.isActive();
        created = experiment.getCreated();
        modified = experiment.getModified();
        activated = experiment.getActivated();
        deactivated = experiment.getDeactivated();
        identityType = experiment.getIdentityType();
        treatments = Lists.transform(
            experiment.getTreatments(),
            new Function<Treatment, TreatmentEntity>() {
                @Override
                public TreatmentEntity apply(Treatment input) {
                    return TreatmentEntity.from(input);
                }
            }
        );
        allocations = Lists.transform(
            experiment.getAllocations(),
            new Function<Allocation, AllocationEntity>() {
                @Override
                public AllocationEntity apply(Allocation input) {
                    return AllocationEntity.from(input);
                }
            }
        );
        overrides = Lists.transform(
            experiment.getOverrides(),
            new Function<TreatmentOverride, TreatmentOverrideEntity>() {
                @Override
                public TreatmentOverrideEntity apply(TreatmentOverride input) {
                    return TreatmentOverrideEntity.from(input);
                }
            }
        );
    }
}