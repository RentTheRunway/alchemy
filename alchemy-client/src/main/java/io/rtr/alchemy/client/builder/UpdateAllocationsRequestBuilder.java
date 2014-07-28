package io.rtr.alchemy.client.builder;

import io.rtr.alchemy.dto.requests.AllocationRequest;
import io.rtr.alchemy.dto.requests.AllocationRequests;
import com.sun.jersey.api.client.WebResource;

public class UpdateAllocationsRequestBuilder {
    private final WebResource.Builder builder;
    private final AllocationRequests allocations;

    public UpdateAllocationsRequestBuilder(WebResource.Builder builder) {
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
        builder.post(allocations);
    }
}
