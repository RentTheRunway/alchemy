package com.rtr.alchemy.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Maps;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.service.jackson.ClassKeyDeserializer;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class AlchemyServiceConfigurationImpl extends Configuration implements AlchemyServiceConfiguration {
    @JsonProperty
    @JsonDeserialize(keyUsing = ClassKeyDeserializer.class)
    private final Map<Class<? extends Identity>, IdentityMapping> identities = Maps.newHashMap();

    @Override
    public Map<Class<? extends Identity>, IdentityMapping> getIdentities() {
        return identities;
    }

    @JsonProperty
    @NotNull
    private final StoreProviderConfiguration provider = null;

    @Override
    public StoreProviderConfiguration getProvider() {
        return provider;
    }

    @JsonProperty
    private final CacheStrategyConfiguration cacheStrategy = null;

    @Override
    public CacheStrategyConfiguration getCacheStrategy() {
        return cacheStrategy;
    }
}
