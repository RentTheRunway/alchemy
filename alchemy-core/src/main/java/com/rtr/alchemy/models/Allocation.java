package com.rtr.alchemy.models;

import com.google.common.base.Objects;

/**
 * Represents a contiguous allocation block of a single treatment in an experiment
 */
public class Allocation {
    private final Treatment treatment;
    private final int offset;
    private final int size;

    public Allocation(Treatment treatment, int offset, int size) {
        this.treatment = treatment;
        this.offset = offset;
        this.size = size;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Allocation)) {
            return false;
        }

        final Allocation other = (Allocation) obj;

        return
            Objects.equal(treatment, other.treatment) &&
            Objects.equal(offset, other.offset) &&
            Objects.equal(size, other.size);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(treatment, offset, size);
    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("treatment", treatment)
                .add("offset", offset)
                .add("size", size)
                .toString();
    }
}
