package com.rtr.alchemy.example.identities;

import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.Segments;

import java.util.Set;

/**
 * An example identity
 */
@Segments({User.SEGMENT_ANONYMOUS, User.SEGMENT_IDENTIFIED})
public class User extends Identity {
    public static final String SEGMENT_ANONYMOUS = "anonymous";
    public static final String SEGMENT_IDENTIFIED = "identified";
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public long computeHash(int seed, Set<String> segments) {
        return identity(seed)
            .putString(name)
            .hash();
    }

    @Override
    public Set<String> computeSegments() {
        return segments(name == null ? SEGMENT_ANONYMOUS : SEGMENT_IDENTIFIED);
    }
}
