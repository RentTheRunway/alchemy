package com.rtr.alchemy.db.mongo.models;

import com.rtr.alchemy.models.Treatment;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;

/**
 * An entity that mirrors Treatment
 * @see com.rtr.alchemy.models.Treatment
 */
@Embedded
public class TreatmentEntity {
    public String name;
    public String description;

    public static TreatmentEntity from(Treatment treatment) {
        return new TreatmentEntity(treatment);
    }

    // Required by Morphia
    private TreatmentEntity() { }

    private TreatmentEntity(Treatment treatment) {
        name = treatment.getName();
        description = treatment.getDescription();
    }
}