package com.rtr.alchemy.dto.identities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("user")
public class UserDto extends IdentityDto {
    private final Long userId;

    @JsonCreator
    public UserDto(@JsonProperty("userId") Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
