package com.rtr.alchemy.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.rtr.alchemy.dto.identities.IdentityDto;

@JsonTypeName("user")
public class UserDto extends IdentityDto {
    private final String name;

    @JsonCreator
    public UserDto(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
