package io.rtr.alchemy.db.mongo.models;

import dev.morphia.annotations.Entity;

import io.rtr.alchemy.models.Treatment;

/**
 * An entity that mirrors Treatment
 *
 * @see io.rtr.alchemy.models.Treatment
 */
@Entity
public class TreatmentEntity {
    public String name;
    public String description;

    // Required by Morphia
    @SuppressWarnings("unused")
    private TreatmentEntity() {}

    private TreatmentEntity(final Treatment treatment) {
        name = treatment.getName();
        description = treatment.getDescription();
    }

    public static TreatmentEntity from(final Treatment treatment) {
        return new TreatmentEntity(treatment);
    }
}
