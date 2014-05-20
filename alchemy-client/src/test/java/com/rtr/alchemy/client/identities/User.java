package com.rtr.alchemy.client.identities;

import com.google.common.collect.Sets;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.Segments;

import java.util.Set;

@Segments({"anonymous", "identified"})
public class User extends Identity {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public long computeHash(int seed) {
        return
            identity(seed)
                .putString(name)
                .hash();
    }

    @Override
    public Set<String> computeSegments() {
        return Sets.newHashSet(name == null ? "anonymous" : "identified");
    }
}
