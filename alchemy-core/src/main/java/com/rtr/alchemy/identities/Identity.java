package com.rtr.alchemy.identities;

/**
 * Identifies a unique entity whose hash code is used for treatments allocation
 */
public abstract class Identity {
    public abstract long getHash(int seed);

    protected IdentityBuilder identity(int seed) {
        return IdentityBuilder.seed(seed);
    }
}
