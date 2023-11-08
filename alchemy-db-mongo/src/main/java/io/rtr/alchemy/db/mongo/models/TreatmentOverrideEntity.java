package io.rtr.alchemy.db.mongo.models;

import dev.morphia.annotations.Embedded;

import io.rtr.alchemy.models.TreatmentOverride;

/**
 * An entity that mirrors TreatmentOverride
 *
 * @see io.rtr.alchemy.models.TreatmentOverride
 */
@Embedded
public class TreatmentOverrideEntity {
    public String name;
    public String treatment;
    public String filter;

    public static TreatmentOverrideEntity from(TreatmentOverride override) {
        return new TreatmentOverrideEntity(override);
    }

    // Required by Morphia
    private TreatmentOverrideEntity() {}

    private TreatmentOverrideEntity(TreatmentOverride override) {
        this.name = override.getName();
        this.treatment = override.getTreatment().getName();
        this.filter = override.getFilter().toString();
    }
}
