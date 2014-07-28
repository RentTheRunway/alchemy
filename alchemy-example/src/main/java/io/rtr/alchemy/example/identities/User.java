package io.rtr.alchemy.example.identities;

import io.rtr.alchemy.identities.Attributes;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;

import java.util.Set;

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
    public long computeHash(int seed, Set<String> hashAttributes, AttributesMap attributes) {
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
