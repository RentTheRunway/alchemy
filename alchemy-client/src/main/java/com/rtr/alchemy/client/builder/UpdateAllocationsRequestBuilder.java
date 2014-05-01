package com.rtr.alchemy.client.builder;

import com.rtr.alchemy.dto.requests.AllocationRequest;
import com.rtr.alchemy.dto.requests.AllocationRequests;
import com.sun.jersey.api.client.WebResource;

public class UpdateAllocationsRequestBuilder {
    private final WebResource.Builder builder;
    private final AllocationRequests allocations;

    public UpdateAllocationsRequestBuilder(WebResource.Builder builder) {
        this.builder = builder;
        this.allocations = AllocationRequests.of();
    }

    public UpdateAllocationsRequestBuilder allocate(final String treatmentName, final int size) {
        allocations.add(new AllocationRequest.Allocate() {
            @Override
            public String getTreatment() {
                return treatmentName;
            }

            @Override
            public Integer getSize() {
                return size;
            }
        });

        return this;
    }

    public UpdateAllocationsRequestBuilder deallocate(final String treatmentName, final int size) {
        allocations.add(new AllocationRequest.Deallocate() {
            @Override
            public String getTreatment() {
                return treatmentName;
            }

            @Override
            public Integer getSize() {
                return size;
            }
        });

        return this;
    }

    public UpdateAllocationsRequestBuilder reallocate(final String treatmentName, final String target, final int size) {
        allocations.add(new AllocationRequest.Reallocate() {
            @Override
            public String getTreatment() {
                return treatmentName;
            }

            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public Integer getSize() {
                return size;
            }
        });

        return this;
    }

    public void apply() {
        builder.post(allocations);
    }
}
