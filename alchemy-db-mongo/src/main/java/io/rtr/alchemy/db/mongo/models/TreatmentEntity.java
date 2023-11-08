package io.rtr.alchemy.db.mongo.models;

import dev.morphia.annotations.Embedded;

import io.rtr.alchemy.models.Treatment;

/**
 * An entity that mirrors Treatment
 *
 * @see io.rtr.alchemy.models.Treatment
 */
@Embedded
public class TreatmentEntity {
    public String name;
    public String description;

    public static TreatmentEntity from(Treatment treatment) {
        return new TreatmentEntity(treatment);
    }

    // Required by Morphia
    private TreatmentEntity() {}

    private TreatmentEntity(Treatment treatment) {
        name = treatment.getName();
        description = treatment.getDescription();
    }
}
