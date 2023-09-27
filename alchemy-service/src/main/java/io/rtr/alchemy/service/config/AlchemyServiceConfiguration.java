package io.rtr.alchemy.service.config;

import io.rtr.alchemy.identities.Identity;
import java.util.Map;

/** The base configuration for the Alchemy Dropwizard service */
public interface AlchemyServiceConfiguration {
    /** Defines a list of known identity types, which are used for assigning users to a treatment */
    Map<Class<? extends Identity>, IdentityMapping> getIdentities();

    /** Defines a store configuration for creating a store provider */
    StoreProviderConfiguration getProvider();

    /** Defines a cache strategy configuration for creating a cache strategy */
    CacheStrategyConfiguration getCacheStrategy();
}
