package io.rtr.alchemy.dto.requests;

import java.util.Optional;

public class UpdateTreatmentRequest {
    private Optional<String> description;

    public UpdateTreatmentRequest() { }

    public UpdateTreatmentRequest(Optional<String> description) {
        this.description = description;
    }

    public void setDescription(Optional<String> description) {
        this.description = description;
    }

    public Optional<String> getDescription() {
        return description;
    }
}
