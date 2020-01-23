package io.rtr.alchemy.client.builder;

import io.rtr.alchemy.dto.requests.AllocationRequest;
import io.rtr.alchemy.dto.requests.AllocationRequests;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

public class UpdateAllocationsRequestBuilder {
    private final Invocation.Builder builder;
    private final AllocationRequests allocations;

    public UpdateAllocationsRequestBuilder(Invocation.Builder builder) {
        this.builder = builder;
        this.allocations = AllocationRequests.of();
    }

    public UpdateAllocationsRequestBuilder allocate(String treatmentName, int size) {
        allocations.add(new AllocationRequest.Allocate(treatmentName,size));

        return this;
    }

    public UpdateAllocationsRequestBuilder deallocate(String treatmentName, int size) {
        allocations.add(new AllocationRequest.Deallocate(treatmentName, size));
        return this;
    }

    public UpdateAllocationsRequestBuilder reallocate(final String treatmentName, final String target, final int size) {
        allocations.add(new AllocationRequest.Reallocate(treatmentName, size, target));
        return this;
    }

    public void apply() {
        builder.post(Entity.entity(allocations, MediaType.APPLICATION_JSON_TYPE));
    }
}
