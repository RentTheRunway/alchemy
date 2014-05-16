package com.rtr.alchemy.db.mongo.models;

import com.rtr.alchemy.models.Allocation;
import org.mongodb.morphia.annotations.Embedded;

import javax.annotation.Nullable;

/**
 * An entity that mirrors Allocation
 * @see com.rtr.alchemy.models.Allocation
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
    private AllocationEntity() { }

    private AllocationEntity(Allocation allocation) {
        this.treatment = allocation.getTreatment().getName();
        this.offset = allocation.getOffset();
        this.size = allocation.getSize();
    }
}