package com.rtr.alchemy.identities;

/**
 * Represents a device with a free form id
 */
public class Device extends Identity {
    private final String deviceId;

    public Device(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public long getHash(int seed) {
        return
            identity(seed)
                .putString(deviceId)
                .hash();
    }
}
