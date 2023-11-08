package io.rtr.alchemy.db.mongo.models;

import io.rtr.alchemy.models.Allocation;
import dev.morphia.annotations.Embedded;

/**
 * An entity that mirrors Allocation
 *
 * @see io.rtr.alchemy.models.Allocation
 */
@Embedded
public class AllocationEntity {
    public String treatment;
    public int offset;
    public int size;

    public static AllocationEntity from(Allocation allocation) {
        return new AllocationEntity(allocation);
    }

    // Required by Morphia
    private AllocationEntity() {}

    private AllocationEntity(Allocation allocation) {
        this.treatment = allocation.getTreatment().getName();
        this.offset = allocation.getOffset();
        this.size = allocation.getSize();
    }
}
