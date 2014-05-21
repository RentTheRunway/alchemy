package com.rtr.alchemy.example.identities;

import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.Segments;

import java.util.Set;

@Segments(Device.SEGMENT_DEVICE)
public class Device extends Identity {
    public static final String SEGMENT_DEVICE = "device";
    private final String id;

    public Device(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public long computeHash(int seed, Set<String> segments) {
        return
            identity(seed)
                .putString(id)
                .hash();
    }

    @Override
    public Set<String> computeSegments() {
        return segments(SEGMENT_DEVICE);
    }
}
