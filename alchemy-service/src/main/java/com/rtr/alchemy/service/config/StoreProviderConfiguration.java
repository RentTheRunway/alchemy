package com.rtr.alchemy.service.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rtr.alchemy.db.ExperimentsStoreProvider;

/**
 * Base configuration object for creating store providers for experiments
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class StoreProviderConfiguration {
    public abstract ExperimentsStoreProvider createProvider();
}
