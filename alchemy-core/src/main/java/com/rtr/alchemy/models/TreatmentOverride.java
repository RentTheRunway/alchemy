package com.rtr.alchemy.models;

import com.google.common.base.Objects;

/**
 * Represents a treatment override assigned to a specific hash value
 */
public class TreatmentOverride {
    private final String name;
    private final long hash;
    private final Treatment treatment;

    public TreatmentOverride(String name, long hash, Treatment treatment) {
        this.name = name;
        this.hash = hash;
        this.treatment = treatment;
    }

    public String getName() {
        return name;
    }

    public long getHash() {
        return hash;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, hash, treatment);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TreatmentOverride)) {
            return false;
        }

        final TreatmentOverride other = (TreatmentOverride) obj;

        return
            Objects.equal(name, other.name) &&
            Objects.equal(hash, other.hash) &&
            Objects.equal(treatment, other.treatment);
    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("name", name)
                .add("hash", hash)
                .add("treatment", treatment)
                .toString();
    }
}
