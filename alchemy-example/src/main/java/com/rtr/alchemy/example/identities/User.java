package com.rtr.alchemy.example.identities;

import com.rtr.alchemy.identities.Attributes;
import com.rtr.alchemy.identities.AttributesMap;
import com.rtr.alchemy.identities.Identity;

import java.util.LinkedHashSet;

/**
 * An example identity
 */
@Attributes({User.ATTR_USER, User.ATTR_ANONYMOUS, User.ATTR_IDENTIFIED, User.ATTR_USER_NAME})
public class User extends Identity {
    public static final String ATTR_ANONYMOUS = "anonymous";
    public static final String ATTR_IDENTIFIED = "identified";
    public static final String ATTR_USER = "user";
    public static final String ATTR_USER_NAME = "user_name";

    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public long computeHash(int seed, LinkedHashSet<String> hashAttributes, AttributesMap attributes) {
        return identity(seed)
            .putString(name)
            .hash();
    }

    @Override
    public AttributesMap computeAttributes() {
        return
            attributes()
                .put(ATTR_USER, true)
                .put(ATTR_USER_NAME, name)
                .put(name == null ? ATTR_ANONYMOUS : ATTR_IDENTIFIED, true)
                .build();
    }
}
