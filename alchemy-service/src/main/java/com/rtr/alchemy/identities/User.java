package com.rtr.alchemy.identities;

/**
 * Represents a specific user given by a numerical id
 */

@IdentityType("user")
public class User extends Identity {
    private final Long userId;

    public User(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public long getHash(int seed) {
        return
            identity(seed)
                .putLong(userId)
                .hash();
    }
}
