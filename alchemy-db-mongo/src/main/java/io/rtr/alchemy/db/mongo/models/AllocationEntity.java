package io.rtr.alchemy.db.mongo.models;

import dev.morphia.annotations.Entity;

import io.rtr.alchemy.models.Allocation;

/**
 * An entity that mirrors Allocation
 *
 * @see io.rtr.alchemy.models.Allocation
 */
@Entity
public class AllocationEntity {
    public String treatment;
    public int offset;
    public int size;

    // Required by Morphia
    @SuppressWarnings("unused")
    private AllocationEntity() {}

    private AllocationEntity(final Allocation allocation) {
        this.treatment = allocation.getTreatment().getName();
        this.offset = allocation.getOffset();
        this.size = allocation.getSize();
    }

    public static AllocationEntity from(final Allocation allocation) {
        return new AllocationEntity(allocation);
    }
}
