package com.rtr.alchemy.dto.requests;

import com.google.common.base.Optional;

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
