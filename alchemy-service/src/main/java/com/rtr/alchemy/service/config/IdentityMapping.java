package com.rtr.alchemy.service.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.dto.identities.IdentityDto;

/**
 * Represents a one-directional mapping of one type to another
 */
public class IdentityMapping {
    private final Class<? extends IdentityDto> dto;
    private final Class<? extends Identity> identity;

    @JsonCreator
    public IdentityMapping(@JsonProperty("dto") Class<? extends IdentityDto> dto,
                           @JsonProperty("identity") Class<? extends Identity> identity) {
        this.dto = dto;
        this.identity = identity;
    }

    public Class<?> getDtoType() {
        return dto;
    }

    public Class<?> getIdentityType() {
        return identity;
    }
}
