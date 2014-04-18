package com.rtr.alchemy.dto.requests;

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
    public abstract String getTreatment();

    @NotNull
    public abstract Integer getSize();

    @JsonTypeName("allocate")
    public static abstract class Allocate extends AllocationRequest {
    }

    @JsonTypeName("deallocate")
    public static abstract class Deallocate extends AllocationRequest {
    }

    @JsonTypeName("reallocate")
    public static abstract class Reallocate extends AllocationRequest {
        @NotNull
        public abstract String getTarget();
    }
}
