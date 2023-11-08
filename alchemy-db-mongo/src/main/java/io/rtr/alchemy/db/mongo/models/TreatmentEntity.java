package io.rtr.alchemy.db.mongo.models;

import io.rtr.alchemy.models.Treatment;
import dev.morphia.annotations.Embedded;

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
