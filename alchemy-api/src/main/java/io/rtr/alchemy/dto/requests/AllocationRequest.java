package io.rtr.alchemy.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.validation.constraints.NotNull;

/**
 * Represents requests for multiple allocation actions
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "action")
@JsonSubTypes({
    @Type(AllocationRequest.Reallocate.class),
    @Type(AllocationRequest.Allocate.class),
    @Type(AllocationRequest.Deallocate.class)
})
public abstract class AllocationRequest {
    @NotNull
    private final Integer treatment;
    @NotNull
    private final Integer size;

    public AllocationRequest(Integer treatment,
                             Integer size) {
        this.treatment = treatment;
        this.size = size;
    }

    public Integer getTreatment() {
        return treatment;
    }

    public Integer getSize() {
        return size;
    }

    @JsonTypeName("allocate")
    public static class Allocate extends AllocationRequest {
        public Allocate(@JsonProperty("treatment") Integer treatment,
                        @JsonProperty("size") Integer size) {
            super(treatment, size);
        }
    }

    @JsonTypeName("deallocate")
    public static class Deallocate extends AllocationRequest {
        public Deallocate(@JsonProperty("treatment") Integer treatment,
                          @JsonProperty("size") Integer size) {
            super(treatment, size);
        }
    }

    @JsonTypeName("reallocate")
    public static class Reallocate extends AllocationRequest {
        @NotNull
        private final Integer target;

        public Reallocate(@JsonProperty("treatment") Integer treatment,
                          @JsonProperty("size") Integer size,
                          @JsonProperty("target") Integer target) {
            super(treatment, size);
            this.target = target;
        }

        public Integer getTarget() {
            return target;
        }
    }
}
