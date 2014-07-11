package com.rtr.alchemy.client.identities;

import com.rtr.alchemy.identities.Attributes;
import com.rtr.alchemy.identities.AttributesMap;
import com.rtr.alchemy.identities.Identity;

import java.util.Set;

@Attributes({"anonymous", "identified"})
public class User extends Identity {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public long computeHash(int seed, Set<String> hashAttributes, AttributesMap attributes) {
        return
            identity(seed)
                .putString(name)
                .hash();
    }

    @Override
    public AttributesMap computeAttributes() {
        return
            name == null ?
            attributes().put("anonymous", true).build() :
            attributes().put("identified", true).build();
    }
}
