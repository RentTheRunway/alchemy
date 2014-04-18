package com.rtr.alchemy.example.identities;

import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.IdentityType;

@IdentityType("user")
public class User extends Identity {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public long getHash(int seed) {
        return identity(seed)
            .putString(name)
            .hash();
    }
}
