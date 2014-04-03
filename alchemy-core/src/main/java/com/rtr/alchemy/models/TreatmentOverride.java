package com.rtr.alchemy.models;

import com.google.common.base.Objects;

/**
 * Represents a treatment override assigned to a specific hash value
 */
public class TreatmentOverride {
    private final String identity;
    private final long hash;
    private final String treatment;

    public TreatmentOverride(String identity, long hash, String treatment) {
        this.identity = identity;
        this.hash = hash;
        this.treatment = treatment;
    }

    public String getIdentity() {
        return identity;
    }

    public long getHash() {
        return hash;
    }

    public String getTreatment() {
        return treatment;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identity, hash, treatment);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TreatmentOverride)) {
            return false;
        }

        TreatmentOverride other = (TreatmentOverride) obj;

        return
            Objects.equal(identity, other.identity) &&
            Objects.equal(hash, other.hash) &&
            Objects.equal(treatment, other.treatment);
    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("identity", identity)
                .add("hash", hash)
                .add("treatment", treatment)
                .toString();
    }
}
