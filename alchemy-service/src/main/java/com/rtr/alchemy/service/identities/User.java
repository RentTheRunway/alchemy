package com.rtr.alchemy.service.identities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.rtr.alchemy.identities.Identity;

@JsonTypeName("user")
public class User extends Identity {
    private final Long userId;

    @JsonCreator
    public User(@JsonProperty("userId") Long userId) {
        this.userId = userId;
    }

    @Override
    public long getHash(int seed) {
        return
            identity(seed)
                .putLong(userId)
                .hash();
    }
}
