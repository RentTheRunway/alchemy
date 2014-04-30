package com.rtr.alchemy.service.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.mapping.Mapper;

/**
 * Represents a one-directional mapping of one type to another
 */
public class IdentityMapping {
    private final Class<? extends IdentityDto> dto;
    private final Class<? extends Mapper> mapper;

    @JsonCreator
    public IdentityMapping(@JsonProperty("dto") Class<? extends IdentityDto> dto,
                           @JsonProperty("mapper") Class<? extends Mapper> mapper) {
        this.dto = dto;
        this.mapper = mapper;
    }

    public Class<? extends IdentityDto> getDtoType() {
        return dto;
    }

    public Class<? extends Mapper> getMapperType() {
        return mapper;
    }
}
