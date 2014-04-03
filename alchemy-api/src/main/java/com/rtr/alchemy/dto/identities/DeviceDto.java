package com.rtr.alchemy.dto.identities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("device")
public class DeviceDto extends IdentityDto {
    private final String deviceId;

    @JsonCreator
    public DeviceDto(@JsonProperty("deviceId") String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
