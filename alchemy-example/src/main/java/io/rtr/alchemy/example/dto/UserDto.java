package io.rtr.alchemy.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.rtr.alchemy.dto.identities.IdentityDto;

/** An example DTO to be used for service payload */
@JsonTypeName("user")
public class UserDto extends IdentityDto {
    private final String name;

    public UserDto(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
