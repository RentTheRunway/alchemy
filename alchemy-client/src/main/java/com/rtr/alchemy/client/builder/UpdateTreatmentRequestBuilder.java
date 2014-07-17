package com.rtr.alchemy.client.builder;

import com.google.common.base.Optional;
import com.rtr.alchemy.dto.requests.UpdateTreatmentRequest;
import com.sun.jersey.api.client.WebResource;

public class UpdateTreatmentRequestBuilder {
    private final WebResource.Builder builder;
    private Optional<String> description;

    public UpdateTreatmentRequestBuilder(WebResource.Builder builder) {
        this.builder = builder;
    }

    public UpdateTreatmentRequestBuilder setDescription(String description) {
        this.description = Optional.fromNullable(description);
        return this;
    }

    public void apply() {
        builder.post(new UpdateTreatmentRequest(description));
    }
}
