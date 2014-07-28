package io.rtr.alchemy.dto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Represents an allocation
 */
public class AllocationDto {
    private final String treatment;
    private final int offset;
    private final int size;

    @JsonCreator
    public AllocationDto(@JsonProperty("treatment") String treatment,
                         @JsonProperty("offset") int offset,
                         @JsonProperty("size") int size) {
        this.treatment = treatment;
        this.offset = offset;
        this.size = size;
    }

    public String getTreatment() {
        return treatment;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(treatment, offset, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AllocationDto)) {
            return false;
        }

        final AllocationDto other = (AllocationDto) obj;

        return
            Objects.equal(treatment, other.treatment) &&
            Objects.equal(offset, other.offset) &&
            Objects.equal(size, other.size);
    }
}
