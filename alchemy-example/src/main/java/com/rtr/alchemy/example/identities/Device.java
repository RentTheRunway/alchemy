package com.rtr.alchemy.example.identities;

import com.rtr.alchemy.identities.Attributes;
import com.rtr.alchemy.identities.AttributesMap;
import com.rtr.alchemy.identities.Identity;

import java.util.LinkedHashSet;

@Attributes({Device.ATTR_DEVICE, Device.ATTR_DEVICE_ID})
public class Device extends Identity {
    public static final String ATTR_DEVICE = "device";
    public static final String ATTR_DEVICE_ID = "device_id";
    private final String id;

    public Device(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public long computeHash(int seed, LinkedHashSet<String> hashAttributes, AttributesMap attributes) {
        return
            identity(seed)
                .putString(id)
                .hash();
    }

    @Override
    public AttributesMap computeAttributes() {
        return attributes()
            .put(ATTR_DEVICE, true)
            .put(ATTR_DEVICE_ID, id)
            .build();
    }
}
