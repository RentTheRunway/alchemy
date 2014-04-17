package com.rtr.alchemy.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.rtr.alchemy.db.ExperimentsDatabaseProvider;
import com.rtr.alchemy.identities.Device;
import com.rtr.alchemy.identities.GeoLocation;
import com.rtr.alchemy.identities.User;
import com.rtr.alchemy.mapping.MapperBuilder;
import com.rtr.alchemy.dto.identities.DeviceDto;
import com.rtr.alchemy.dto.identities.GeoLocationDto;
import com.rtr.alchemy.dto.identities.UserDto;
import com.rtr.alchemy.mapping.OrikaMapperBuilder;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The base configuration for the Alchemy dropwizard service
 */
public class AlchemyServiceConfiguration extends Configuration {
    /**
     * Defines a list of known identity types, which are used for assigning users to a treatment
     */
    @SuppressWarnings("unchecked")
    @JsonProperty
    private final List<IdentityMapping> identityTypes = Lists.newArrayList(
        new IdentityMapping(DeviceDto.class, Device.class),
        new IdentityMapping(GeoLocationDto.class, GeoLocation.class),
        new IdentityMapping(UserDto.class, User.class)
    );

    public List<IdentityMapping> getIdentityTypes() {
        return identityTypes;
    }

    /**
     * Defines a mapper implementation to use for mapping DTOs to Domain Objects
     */
    @JsonProperty
    private final Class<? extends MapperBuilder> mapper = OrikaMapperBuilder.class;
    public Class<? extends MapperBuilder> getMapper() {
        return mapper;
    }

    @JsonProperty
    @NotNull
    private final ExperimentsDatabaseProvider provider = null;
    public ExperimentsDatabaseProvider getProvider() {
        return provider;
    }
}
