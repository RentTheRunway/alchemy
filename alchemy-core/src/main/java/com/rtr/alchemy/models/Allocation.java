package com.rtr.alchemy.models;

import com.google.common.base.Objects;

/**
 * Represents a contiguous allocation block of a single treatment in an experiment
 */
public class Allocation {
    private final String treatment;
    private final int size;

    public Allocation(String treatment, int size) {
        this.treatment = treatment;
        this.size = size;
    }

    public String getTreatment() {
        return treatment;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Allocation)) {
            return false;
        }

        Allocation other = (Allocation) obj;

        return
            Objects.equal(treatment, other.treatment) &&
            Objects.equal(size, other.size);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(treatment, size);
    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("treatment", treatment)
                .add("size", size)
                .toString();
    }
}
