package io.rtr.alchemy.db.mongo.models;

import dev.morphia.annotations.Entity;

import io.rtr.alchemy.models.TreatmentOverride;

/**
 * An entity that mirrors TreatmentOverride
 *
 * @see io.rtr.alchemy.models.TreatmentOverride
 */
@Entity
public class TreatmentOverrideEntity {
    public String name;
    public String treatment;
    public String filter;

    // Required by Morphia
    @SuppressWarnings("unused")
    private TreatmentOverrideEntity() {}

    private TreatmentOverrideEntity(final TreatmentOverride override) {
        this.name = override.getName();
        this.treatment = override.getTreatment().getName();
        this.filter = override.getFilter().toString();
    }

    public static TreatmentOverrideEntity from(final TreatmentOverride override) {
        return new TreatmentOverrideEntity(override);
    }
}
