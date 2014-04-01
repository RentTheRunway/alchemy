package com.rtr.alchemy.service.identities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.rtr.alchemy.identities.Identity;

@JsonTypeName("device")
public class Device extends Identity {
    private final String deviceId;

    @JsonCreator
    public Device(@JsonProperty("deviceId") String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public long getHash(int seed) {
        return
            identity(seed)
                .putString(deviceId)
                .hash();
    }
}
