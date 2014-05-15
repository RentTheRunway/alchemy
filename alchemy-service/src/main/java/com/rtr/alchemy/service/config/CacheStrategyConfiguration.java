package com.rtr.alchemy.service.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rtr.alchemy.caching.CacheStrategy;

/**
 * Base configuration object for creating a cache strategy
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class CacheStrategyConfiguration {
    public abstract CacheStrategy createStrategy();
}
