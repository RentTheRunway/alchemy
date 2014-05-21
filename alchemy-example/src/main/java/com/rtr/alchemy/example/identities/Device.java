package com.rtr.alchemy.example.identities;

import com.rtr.alchemy.identities.Identity;

import java.util.Set;

public class Device extends Identity {
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
}
