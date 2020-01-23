package io.rtr.alchemy.client.builder;

import io.rtr.alchemy.dto.requests.UpdateTreatmentRequest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

public class UpdateTreatmentRequestBuilder {
    private final Invocation.Builder builder;
    private Optional<String> description;

    public UpdateTreatmentRequestBuilder(Invocation.Builder builder) {
        this.builder = builder;
    }

    public UpdateTreatmentRequestBuilder setDescription(String description) {
        this.description = Optional.ofNullable(description);
        return this;
    }

    public void apply() {
        builder.post(Entity.entity(new UpdateTreatmentRequest(description), MediaType.APPLICATION_JSON_TYPE));
    }
}
