package com.rtr.alchemy.identities;

import com.google.common.base.Preconditions;

/**
 * Identifies a unique entity whose hash code is used for treatments allocation
 */
public abstract class Identity {
    private final String type;

    public Identity() {
        final IdentityType typeAnnotation = getClass().getAnnotation(IdentityType.class);

        Preconditions.checkState(
            typeAnnotation != null &&
            typeAnnotation.value() != null &&
            !typeAnnotation.value().isEmpty(),
            "identity class %s must specify a valid @%s annotation",
            getClass().getName(),
            IdentityType.class.getSimpleName()
        );


        type = typeAnnotation.value();
    }

    public abstract long getHash(int seed);

    protected IdentityBuilder identity(int seed) {
        return IdentityBuilder.seed(seed);
    }

    public String getType() {
        return type;
    }
}
